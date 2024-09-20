import AppKit
import Combine
import Foundation

class CryptoViewModel: ObservableObject {
  static let shared = CryptoViewModel()
  
  @Published var items = SortedArray<Crypto>()

  @Published var crypto: Crypto? {
    didSet {
      if let crypto = crypto {
        if 
          !crypto.image.isLoaded &&
          !crypto.image.isLoading {
          fetchCryptoImage(for: crypto)
        }
      }
    }
  }
  
  private let decoder = JSONDecoder()
  private var cancellableBag = Set<AnyCancellable>()
  
  private let tickerWSClient = JsonWSClient<Ticker>()
  
  init() {
    fetchAllTickers()
  }
  
  // MARK: - Public
  
  public func findByText(_ text: String) -> [Crypto] {
    guard !text.isEmpty else { return items.allElements() }
    
    return items.allElements().filter { crypto in
      crypto.market.contains(text) ||
      text.contains(crypto.market) ||
      crypto.nameKr.contains(text) ||
      text.contains(crypto.nameKr) ||
      crypto.nameEn.contains(text) ||
      text.contains(crypto.nameEn)
    }
  }
  
  public func findByMarket(_ market: String) -> Crypto? {
    guard let index = items.firstIndex(where: { $0.market == market }) else {
      return nil
    }
    
    return items[index]
  }
  
  // MARK: - Privacy
  
  private func fetchAllTickers() {
    guard let url = APIEndpoint.tickers.url else { return }
    
    APIClient.shared.request(url: url)
      .receive(on: RunLoop.main)
      .sink { completion in
        switch completion {
        case .finished:
          break
        case .failure(let error):
          print("Failed with error: \(error)")
        }
      } receiveValue: { [weak self] (data, response) in
        guard response.statusCode < 300, let self else { return }
        do {
          let tickers = try decoder.decode([Ticker].self, from: data)
          tickers.forEach { self.updateItem($0) }
          
          // success handler
          crypto = items.allElements().first(where: {$0.market == "KRW-BTC" })
          fetchRealtimeTicker()
        } catch {
          // TODO: decode error handling (?)
        }
      }
      .store(in: &cancellableBag)
  }
  
  private func fetchRealtimeTicker() {
    // crypto 정보가 없다면, 다시 fetch
    
    // fetch ticker
    guard let tickerUrl = WSEndpoint.ticker.url else { return }

    tickerWSClient.connect(url: tickerUrl)
      .receive(on: RunLoop.main)
      .sink { [weak self] ticker in
        self?.updateItem(ticker)
      }
      .store(in: &cancellableBag)
  }
  
  private func fetchCrypto() {
    guard let url = URL(string: "http://localhost:8090/api/cryptos") else { return }
    let request = URLRequest(url: url)
    
    URLSession.shared.dataTaskPublisher(for: request)
      .map(\.data)
      .decode(type: [CryptoDto].self, decoder: decoder)
      .map { cryptoList in
        cryptoList.map{$0.toCrypto()}
      }
      .receive(on: RunLoop.main)
      .sink { completion in
        switch completion {
        case .finished:
          break
        case .failure(let error):
          print("Failed with error: \(error)")
        }
      } receiveValue: { [weak self] cryptos in
        self?.items.insertAll(cryptos)
      }
      .store(in: &cancellableBag)
  }
  
  private func updateItem(_ ticker: Ticker) {
    let newCrypto = Crypto(
      market: ticker.code,
      nameKr: ticker.code,
      nameEn: ticker.code,
      ticker: ticker
    )
    
    if crypto?.market == newCrypto.market {
      newCrypto.image = crypto?.image ?? CryptoImage()
      self.crypto = newCrypto
    }
    
    if let idx = items.firstIndex(where: { $0.market == ticker.code }) {
      newCrypto.image = items[idx].image
      items.update(idx, with: newCrypto)
    } else {
      items.insert(newCrypto)
    }
  }
  
  private func fetchCryptoImage(for crypto: Crypto) {
    guard 
      !crypto.image.isLoading,
      !crypto.image.isLoaded else {
      return
    }
    
    crypto.image.isLoading = true
    
    // 1. FileManager를 찾아본다.
    guard let symbol = symbolToFullName[crypto.market] else {
      return
    }
    
    let cacheDirectory = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask)[0]
    let cachedUrl = cacheDirectory.appendingPathComponent(symbol, conformingTo: .png)
    
    if
      FileManager.default.fileExists(atPath: cachedUrl.path),
      let localData = try? Data(contentsOf: cachedUrl) {
      let image = NSImage(data: localData)
      self.updateCryptoImageState(isLoading: true, isLoaded: true, with: image, for: crypto)
      return
    }
    
    // 2. 실패 시 코인게코에서 얻어온다.
        
    let apiUrl = "https://api.coingecko.com/api/v3/coins/\(symbol)"
    guard let url = URL(string: apiUrl) else { return }
    
    APIClient.shared.request(url: url)
      .receive(on: RunLoop.main)
      .sink{ [weak self] completion in
        switch completion {
        case .finished:
          break
        case .failure(let error):
          print("Failed to fetch coin data: \(error.localizedDescription)")
          self?.updateCryptoImageState(for: crypto)
        }
      } receiveValue: { [weak self] (data, response) in
        guard response.statusCode < 300 else {
          self?.updateCryptoImageState(for: crypto)
            return
        }
        
        do {
          // JSON 데이터 파싱
          if let json = try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any],
             let imageInfo = json["image"] as? [String: String],
             let imageUrl = imageInfo["thumb"] {
            self?.loadCryptoImage(from: imageUrl, for: crypto)
          }
        } catch {
          print("Failed to parse JSON: \(error.localizedDescription)")
          self?.updateCryptoImageState(for: crypto)
        }
      }
      .store(in: &cancellableBag)
  }
  
  private func loadCryptoImage(from urlString: String, for crypto: Crypto) {
    guard let url = URL(string: urlString) else { return }
    APIClient.shared.fetchImageData(url: url)
      .receive(on: RunLoop.main)
      .sink { [weak self] completion in
        switch completion {
        case .finished:
          break
        case .failure(_):
          self?.updateCryptoImageState(for: crypto)
        }
      } receiveValue: { [weak self] (data, response) in
        guard let data, let image = NSImage(data: data) else { return }
        self?.updateCryptoImageState(isLoading: true, isLoaded: true, with: image, for: crypto)
      }
      .store(in: &cancellableBag)
  }
  
  private func updateCryptoImageState(
    isLoading: Bool = false,
    isLoaded: Bool = false,
    with image: NSImage? = nil,
    for crypto: Crypto
  ) {
    if let index = items.firstIndex(where: { $0.market == crypto.market }) {
      let updatedCrypto = items[index]
      let cryptoImage = CryptoImage(image: image, isLoading: isLoading, isLoaded: isLoaded)
      
      self.crypto?.image = cryptoImage
    }
  }
}

let symbolToFullName: [String: String] = [
    "KRW-BTC": "bitcoin",
    "KRW-ETH": "ethereum",
    "KRW-XRP": "ripple",
    "KRW-ADA": "cardano",
    "KRW-BCH": "bitcoin-cash",
    "KRW-LTC": "litecoin",
    "KRW-EOS": "eos",
    "KRW-XLM": "stellar",
    "KRW-DOT": "polkadot",
    "KRW-TRX": "tron",
    "KRW-BNB": "binancecoin",
    "KRW-SOL": "solana",
    "KRW-AVAX": "avalanche",
    "KRW-UNI": "uniswap",
    "KRW-ATOM": "cosmos",
    "KRW-LINK": "chainlink",
    "KRW-ALGO": "algorand",
    "KRW-FTM": "fantom",
    "KRW-SAND": "the-sandbox",
    "KRW-MANA": "decentraland",
    "KRW-AXS": "axie-infinity",
    "KRW-NEO": "neo",
    "KRW-KLAY": "klaytn",
    "KRW-ICX": "icon",
    "KRW-QTUM": "qtum",
    "KRW-VET": "vechain",
    "KRW-CHZ": "chiliz",
    "KRW-MATIC": "polygon",
    "KRW-ENJ": "enjincoin",
    "KRW-THETA": "theta",
    "KRW-HBAR": "hedera-hashgraph",
    "KRW-XEM": "nem",
    "KRW-ZIL": "zilliqa",
    "KRW-OMG": "omg-network",
    "KRW-ANKR": "ankr",
    "KRW-AAVE": "aave",
    "KRW-BAT": "basic-attention-token"
    // 필요한 심볼-전체 이름 추가 가능
]

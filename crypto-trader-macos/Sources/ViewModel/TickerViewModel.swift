import Combine
import Foundation

class TickerViewModel: ObservableObject {
  @Published var items = SortedArray<Crypto>()
  @Published var crypto: Crypto?
  private var code: String = "KRW-BTC"
  
  private let decoder = JSONDecoder()
  private var cancellableBag = Set<AnyCancellable>()
  
  private let tickerWebSocketManager = JsonWebSocketManager<Ticker>()
  
  init() {
    // crypto 최초 입력 필요..
    self.crypto = Crypto(code: "KRW-BTC", nameKr: "비트코인", nameEn: "bitcoin", ticker: Ticker())
  }
  
  public func fetchTicker() {
    // crypto 정보가 없다면, 다시 fetch
    
    // fetch ticker
    let tickerUrl = "ws://127.0.0.1:8090/ticker"
    tickerWebSocketManager.connect(url: tickerUrl)
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
      code: ticker.code,
      nameKr: ticker.code,
      nameEn: ticker.code,
      ticker: ticker
    )
    
    if newCrypto.code == code {
      self.crypto = newCrypto
    }
    
    if items.allElements().contains(where: { $0.code == ticker.code }) {
       items.update(where: { $0.code == ticker.code }, with: newCrypto)
    } else {
      items.insert(newCrypto)
    }
  }
}

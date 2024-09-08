import Combine
import Foundation

class CryptoListViewModel: ObservableObject {
  @Published var items = SortedArray<Crypto>()
  
  private let decoder = JSONDecoder()
  private var cancellableBag = Set<AnyCancellable>()
  
  private let tickerWebSocketManager = WebSocketManager()
  
  init() {
//    fetchCrypto()
  }
  
  public func fetchTicker() {
    // crypto 정보가 없다면, 다시 fetch
    
    // fetch ticker
    let tickerUrl = "ws://127.0.0.1:8090/ticker"
    tickerWebSocketManager.connect(url: tickerUrl) { [weak self] result in
      switch result {
      case .success(let message):
        switch message {
        case .string(let json): // 이거?
          guard let data = json.data(using: .utf8),
                let ticker = try self?.decoder.decode(Ticker.self, from: data) else {
            print("decode failure")
            return
          }
          DispatchQueue.main.async {
            self?.updateItem(ticker)
          }
        default:
          break
        }
      case .failure(let error):
        print("error? \(error)")
        // TODO: throw err
      }
    }
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
    
    if items.allElements().contains(where: { $0.code == ticker.code }) {
       items.update(where: { $0.code == ticker.code }, with: newCrypto)
    } else {
      items.insert(newCrypto)
    }
  }
}

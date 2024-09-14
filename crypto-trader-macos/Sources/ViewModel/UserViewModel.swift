import Combine
import Foundation

class UserViewModel: ObservableObject {
  @Published var user: User? // temp anon user
  
  private let decoder = JSONDecoder()
  private var cancellableBag = Set<AnyCancellable>()
  
  init() {
    let btc = Crypto(market: "KRW-BTC", nameKr: "비트코인", nameEn: "bitcoin", ticker: Ticker(
      market: "KRW-BTC",
      tradePrice: 70000,
      changePrice: 10,
      changeRate: 1.0,
      accTradePrice24h: 800
    ))
    
    let xrp = Crypto(market: "KRW-XRP", nameKr: "리플", nameEn: "xripple", ticker: Ticker(
      market: "KRW-XRP",
      tradePrice: 60000,
      changePrice: 100,
      changeRate: 1.0,
      accTradePrice24h: 800
    ))
    
    let cryptoAssetList = [
      CryptoAsset(crypto: btc, amount: 10, avgPrice: 710),
      CryptoAsset(crypto: xrp, amount: 110, avgPrice: 1000),
    ]
    
    user = User(
      account: Account(number: "123", currency: "KRW", balance: 1000000000, locked: 0, avgBuyPrice: 1000000), assets: cryptoAssetList
    )
  }
  
  func fetchUserInfo() {
    let urlString = "http://localhost:8090/api/users/info"
    guard let url = URL(string: urlString) else { return }
    let request = URLRequest(url: url)
    
    URLSession.shared.dataTaskPublisher(for: request)
      .map(\.data)
      .decode(type: UserInfo.self, decoder: decoder)
      .receive(on: RunLoop.main)
      .sink { completion in
        switch completion {
        case .finished:
          break
        case .failure(let error):
          print("Failed with error: \(error)")
        }
      } receiveValue: { [weak self] user in
        
        let assets: [CryptoAsset] = user.assets.compactMap {
          guard let crypto = CryptoViewModel.shared.findByMarket($0.market) else { return nil }
          return CryptoAsset(crypto: crypto, amount: $0.amount, avgPrice: $0.avgPrice)
        }
        
        self?.user = User(
          account: user.account,
          assets: assets
        )
      }
      .store(in: &cancellableBag)
  }
}

import Combine
import Foundation

class UserViewModel: ObservableObject {
  static let shared = UserViewModel()
  
  @Published var user: User? // temp anon user
  
  private let decoder = JSONDecoder()
  private var cancellableBag = Set<AnyCancellable>()
  
  init() {
    // anon user
    user = User(
      account: Account(number: "123", currency: "KRW", balance: 1000000000, locked: 0, avgBidPrice: 1000000), 
      assets: []
    )
    
    // real user
    login()
  }
  
  func login(_ username: String = "user1") {
    guard let url = APIEndpoint.login.url else { return }
    
    APIClient.shared.request(url: url, post: true, param: ["username": username])
      .receive(on: RunLoop.main)
      .sink { completion in
        switch completion {
        case .finished:
          break
        case .failure(let error):
          print("Failed with error: \(error)")
        }
      } receiveValue: { [weak self] (data, response) in
        guard 
          response.statusCode < 300,
          let token = String(data: data, encoding: .utf8)
        else { return }
        APIClient.shared.setToken(token)
        self?.fetchUserInfo()
      }
      .store(in: &cancellableBag)
  }
  
  func fetchUserInfo() {
    guard let url = APIEndpoint.userInfo.url else { return }
    
    APIClient.shared.request(url: url)
      .filter { (_, response) in response.statusCode < 300 }
      .map(\.0)
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
          guard let crypto = CryptoViewModel.shared.findByMarket($0.market) else {
            print("market에 대한 크립토를 찾을 수 없습니다.")
            return nil
          }
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

import Combine
import Foundation
import SwiftUI

class OrderViewModel: ObservableObject {
  @Published var currentSide: OrderSide = .BID
  @Published var price: Double = 81000000
  @Published var amount: Double = 1
  
  var market: String = "KRW-BTC" // 이건 언제 넣어야 할까..? 일단 받는다고 가정.. ㅋㅋ
  var total: Double { price * amount }
  
  private var cancellableBag = Set<AnyCancellable>()
  let encoder = JSONEncoder()
  
  
  // MARK: - Public
  func order(account: Account) {
    if currentSide == .BID {
      bid(account: account)
    } else {
      ask(account: account)
    }
    
    orderRequest()
  }
  
  func bid(account: Account) {
    guard account.balance >= total else {
      // TODO: error handling
      return
    }
  }
  
  func ask(account: Account) {
    // TODO
  }
  
  private func orderRequest() {
    guard let url = APIEndpoint.orderCreate.url else { return }
    
    let dto = OrderCreateDto(
      market: market,
      side: currentSide.rawValue,
      volume: amount,
      price: price
    )
    
    do {
      let body = try encoder.encode(dto)
      print("data? \(body.count)")
      
      APIClient.shared.request(url: url, post: true, body: body)
        .sink(receiveCompletion: { completion in
          switch completion {
          case .finished:
            print("Order creation request finished")
          case .failure(let error):
            print("Order creation request failed with error: \(error)")
          }
        }, receiveValue: { (data, response) in
          guard response.statusCode < 300 else { return }
          print("Received response data: \(String(data: data, encoding: .utf8))")
        })
        .store(in: &cancellableBag)
    } catch {
      print("Failed to encode OrderCreateDto: \(error)")
    }
  }
  
  func fetchOrders() {
    
  }
}

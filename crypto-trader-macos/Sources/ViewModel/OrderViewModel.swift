import Combine
import Foundation
import SwiftUI

class OrderViewModel: ObservableObject {
  @Published var currentSide: OrderSide = .bid
  @Published var price: Double = 0
  @Published var amount: Double = 0
  
  var market: String = ""
  
  var total: Double { price * amount }
  
  
  // MARK: - Public
  func order(account: Account) {
    if currentSide == .bid {
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
    
  }
  
  private func orderRequest() {
    let urlString = "localhost:8090/api/orders"
    
  }
  
  func fetchOrders() {
    
  }
}

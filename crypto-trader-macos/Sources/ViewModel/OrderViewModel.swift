import Combine
import Foundation
import SwiftUI

class OrderViewModel: ObservableObject {
  @Published var selectedSide: OrderSide = .bid
  
  // MARK: - Public
  func order(market: String, price: Double, amount: Double) {
    
  }
  
}

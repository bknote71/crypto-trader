import Foundation

struct OrderInfo: Identifiable {
  let id = UUID().uuidString
  let executionTime: String
  let crypto: String
  let market: String
  let side: String
  let amount: String
  let price: String
  let total: String
  let fee: String
  let finalValue: String
  let orderTime: String
}

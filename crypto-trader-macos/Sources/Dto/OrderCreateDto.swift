import Foundation

struct OrderCreateDto: Encodable {
  let market: String
  let side: String
  let volume: Double
  let price: Double
}

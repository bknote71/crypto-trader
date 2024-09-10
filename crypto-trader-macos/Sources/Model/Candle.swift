import Foundation

struct Candle: Decodable {
  let `open`: Double
  let close: Double
  let high: Double
  let low: Double
  let time: Date
}

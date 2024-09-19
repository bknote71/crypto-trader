import Foundation

struct Candle: Decodable {
  let `open`: Double
  let close: Double
  let high: Double
  let low: Double
  let time: Date
  let volume: Double
  
  enum CodingKeys: CodingKey {
    case `open`
    case close
    case high
    case low
    case time
    case volume
  }
}

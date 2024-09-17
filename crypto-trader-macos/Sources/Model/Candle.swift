import Foundation

struct Candle: Decodable {
  let `open`: Double
  let close: Double
  let high: Double
  let low: Double
  let time: Date
  var x: Date = Date.now
  
  enum CodingKeys: CodingKey {
    case `open`
    case close
    case high
    case low
    case time
  }
}

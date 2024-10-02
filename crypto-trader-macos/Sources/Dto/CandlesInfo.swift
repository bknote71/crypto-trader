import Foundation

struct CandlesInfo: Decodable {
  let startDate: Double
  let endDate: Double
  let count: Int
  let candles: [Data]
}

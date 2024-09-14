import Foundation

struct Crypto: Comparable, Equatable, Decodable {
  let market: String
  let nameKr: String
  let nameEn: String
  let ticker: Ticker
  
  var currentPrice: Double {
    ticker.tradePrice
  }
  
  
  static func < (lhs: Crypto, rhs: Crypto) -> Bool {
    return lhs.ticker.accTradePrice24h > rhs.ticker.accTradePrice24h
  }
  
  static func == (lhs: Crypto, rhs: Crypto) -> Bool {
    return lhs.market == rhs.market && lhs.ticker == rhs.ticker
  }
}

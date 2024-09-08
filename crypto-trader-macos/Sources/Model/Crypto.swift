import Foundation

struct Crypto: Comparable, Equatable {
  let code: String
  let nameKr: String
  let nameEn: String
  let ticker: Ticker
  
  
  static func < (lhs: Crypto, rhs: Crypto) -> Bool {
    return lhs.ticker.accTradePrice24h > rhs.ticker.accTradePrice24h
  }
  
  static func == (lhs: Crypto, rhs: Crypto) -> Bool {
    return lhs.code == rhs.code && lhs.ticker == rhs.ticker
  }
}

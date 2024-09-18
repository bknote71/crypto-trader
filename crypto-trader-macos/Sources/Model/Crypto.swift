import AppKit
import Foundation

struct CryptoItem: Decodable {
  let market: String
  let nameKr: String
  let nameEn: String
}

class Crypto: Comparable, Equatable {
  let market: String
  let nameKr: String
  let nameEn: String
  var ticker: Ticker
  var image: CryptoImage
  
  init(market: String, nameKr: String, nameEn: String, ticker: Ticker, image: CryptoImage = CryptoImage()) {
    self.market = market
    self.nameKr = nameKr
    self.nameEn = nameEn
    self.ticker = ticker
    self.image = image
  }
  
  var currentPrice: Double {
    ticker.tradePrice
  }
  
  enum CodingKeys: CodingKey {
    case market
    case nameKr
    case nameEn
    case ticker
  }
  
  
  static func < (lhs: Crypto, rhs: Crypto) -> Bool {
    return lhs.ticker.accTradePrice24h > rhs.ticker.accTradePrice24h
  }
  
  static func == (lhs: Crypto, rhs: Crypto) -> Bool {
    return lhs.market == rhs.market && lhs.ticker == rhs.ticker
  }
}

struct CryptoImage {
  var image: NSImage? = nil
  var isLoading: Bool = false
  var isLoaded: Bool = false
}

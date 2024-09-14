struct Ticker: Decodable, Equatable {
  let market: String
  let tradePrice: Double
  let changePrice: Double
  let changeRate: Double
  let accTradePrice24h: Double
  
  init() {
    market = ""
    tradePrice = 0
    changePrice = 0
    changeRate = 0
    accTradePrice24h = 0
  }
  
  init(market: String, tradePrice: Double, changePrice: Double, changeRate: Double, accTradePrice24h: Double) {
    self.market = market
    self.tradePrice = tradePrice
    self.changePrice = changePrice
    self.changeRate = changeRate
    self.accTradePrice24h = accTradePrice24h
  }
  
  enum CodingKeys: String, CodingKey {
    case market
    case tradePrice = "trade_price"
    case changePrice = "change_price"
    case changeRate = "change_rate"
    case accTradePrice24h = "acc_trade_price_24h"
  }
  
  // 값이 없을 때의 처리
  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    market = try container.decode(String.self, forKey: .market)
    tradePrice = try container.decode(Double.self, forKey: .tradePrice)
    changePrice = try container.decodeIfPresent(Double.self, forKey: .changePrice) ?? 0
    changeRate = try container.decodeIfPresent(Double.self, forKey: .changeRate) ?? 0
    accTradePrice24h = try container.decode(Double.self, forKey: .accTradePrice24h)
  }
  
  static func == (lhs: Ticker, rhs: Ticker) -> Bool {
    return lhs.market == rhs.market &&
    lhs.tradePrice == rhs.tradePrice &&
    lhs.changePrice == rhs.changePrice &&
    lhs.changeRate == rhs.changeRate &&
    lhs.accTradePrice24h == rhs.accTradePrice24h
  }
}

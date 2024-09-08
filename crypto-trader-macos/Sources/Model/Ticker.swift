struct Ticker: Decodable, Equatable {
  let code: String
  let tradePrice: Double
  let changePrice: Double
  let changeRate: Double
  let accTradePrice24h: Double
  
  init() {
    code = ""
    tradePrice = 0
    changePrice = 0
    changeRate = 0
    accTradePrice24h = 0
  }
  
  enum CodingKeys: String, CodingKey {
    case code
    case tradePrice = "trade_price"
    case changePrice = "change_price"
    case changeRate = "change_rate"
    case accTradePrice24h = "acc_trade_price_24h"
  }
  
  // 값이 없을 때의 처리
  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    code = try container.decode(String.self, forKey: .code)
    tradePrice = try container.decode(Double.self, forKey: .tradePrice)
    changePrice = try container.decodeIfPresent(Double.self, forKey: .changePrice) ?? 0
    changeRate = try container.decodeIfPresent(Double.self, forKey: .changeRate) ?? 0
    accTradePrice24h = try container.decode(Double.self, forKey: .accTradePrice24h)
  }
  
  static func == (lhs: Ticker, rhs: Ticker) -> Bool {
    return lhs.code == rhs.code &&
    lhs.tradePrice == rhs.tradePrice &&
    lhs.changePrice == rhs.changePrice &&
    lhs.changeRate == rhs.changeRate &&
    lhs.accTradePrice24h == rhs.accTradePrice24h
  }
}

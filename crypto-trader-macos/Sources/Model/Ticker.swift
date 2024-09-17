struct Ticker: Decodable, Equatable {
  let code: String
  let tradePrice: Double
  let accTradePrice24h: Double
  let signedChangePrice: Double
  let signedChangeRate: Double
  let highPrice: Double
  let accTradeVolume24h: Double
  
  
  init() {
    code = ""
    tradePrice = 0
    accTradePrice24h = 0
    signedChangePrice = 0
    signedChangeRate = 0
    highPrice = 0
    accTradeVolume24h = 0
  }
  
  init(
    code: String,
    tradePrice: Double,
    accTradePrice24h: Double,
    signedChangePrice: Double,
    signedChangeRate: Double,
    highPrice: Double,
    accTradeVolume24h: Double
  ) {
    self.code = code
    self.tradePrice = tradePrice
    self.accTradePrice24h = accTradePrice24h
    self.signedChangePrice = signedChangePrice
    self.signedChangeRate = signedChangeRate
    self.highPrice = highPrice
    self.accTradeVolume24h = accTradeVolume24h
  }
  
  enum CodingKeys: String, CodingKey {
    case code
    case tradePrice = "trade_price"
    case accTradePrice24h = "acc_trade_price_24h"
    case signedChangePrice = "signed_change_price"
    case signedChangeRate = "signed_change_rate"
    case highPrice = "high_price"
    case accTradeVolume24h = "acc_trade_volume_24h"
  }
  
  // 값이 없을 때의 처리
  init(from decoder: Decoder) throws {
    let container = try decoder.container(keyedBy: CodingKeys.self)
    code = try container.decode(String.self, forKey: .code)
    tradePrice = try container.decode(Double.self, forKey: .tradePrice)
    accTradePrice24h = try container.decode(Double.self, forKey: .accTradePrice24h)
    signedChangePrice = try container.decodeIfPresent(Double.self, forKey: .signedChangePrice) ?? 0
    signedChangeRate = try container.decodeIfPresent(Double.self, forKey: .signedChangeRate) ?? 0
    highPrice = try container.decodeIfPresent(Double.self, forKey: .highPrice) ?? 0
    accTradeVolume24h = try container.decodeIfPresent(Double.self, forKey: .accTradeVolume24h) ?? 0
  }
  
  static func == (lhs: Ticker, rhs: Ticker) -> Bool {
    return lhs.code == rhs.code &&
    lhs.tradePrice == rhs.tradePrice &&
    lhs.accTradePrice24h == rhs.accTradePrice24h &&
    lhs.signedChangePrice == rhs.signedChangePrice &&
    lhs.signedChangeRate == rhs.signedChangeRate &&
    lhs.highPrice == rhs.highPrice &&
    lhs.accTradeVolume24h == rhs.accTradeVolume24h
  }
}

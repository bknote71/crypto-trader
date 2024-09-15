struct Account: Decodable {
  let number: String // 계좌번호
  let currency: String // 화폐 단위
  let balance: Double // 주문가능 금액
  let locked: Double // 주문 중 묶여있는 금액
  let avgBidPrice: Double // 매수 평균가
  var totalBidAmount: Double = 123456 // temp
  
  enum CodingKeys: CodingKey {
    case number
    case currency
    case balance
    case locked
    case avgBidPrice
  }
  
  // 총 보유자산 계산
  var holdCash: Double {
    balance + locked
  }
  
  var totalAsset: Double {
    return totalBidAmount + holdCash
  }

  // 총 평가손익 계산 (총 평가 금액이 필요하므로 가정)
  var totalProfitLoss: Double {
    return avgBidPrice - totalBidAmount
  }

      // 총 평가손익률 계산
  var totalProfitLossRate: Double {
    return (totalProfitLoss / totalBidAmount) * 100
  }
}

struct CryptoAssets {
  // TODO: Sorted Array
  var cryptoAssets = SortedArray<CryptoAsset>()
  
  init(assets: [CryptoAsset]) {
    self.cryptoAssets.insertAll(assets)
  }
  
  // 전체 매수금액 계산
  func totalPurchaseValue() -> Double {
    return cryptoAssets.allElements().reduce(0) { $0 + $1.purchaseValue }
  }
  
  // 특정 market의 비율을 전체 매수금액을 기준으로 계산하는 함수
  // 특정 자산의 비율 계산
  func calculateRate(for asset: CryptoAsset) -> Double {
    let totalValue = totalPurchaseValue()
    return totalValue > 0 ? (asset.purchaseValue / totalValue) * 100 : 0
  }
  
  // 특정 인덱스에 대한 각도 계산 (degrees만 반환)
  func degrees(for index: Int) -> Double {
    // 인덱스 이전까지의 자산의 비율 합산
    let valueSum = cryptoAssets.allElements().prefix(index).map { calculateRate(for: $0) }.reduce(0, +)
    // 각도 계산: (비율 합산 / 100) * 360도
    return (valueSum / 100) * 360
  }
}

struct CryptoAsset: Identifiable, Comparable {
  let crypto: Crypto
  let amount: Double // 보유량
  let avgPrice: Double // 매수 평균가
  
  var id: String {
    crypto.market
  }
  
  static func < (lhs: CryptoAsset, rhs: CryptoAsset) -> Bool {
    lhs.purchaseValue > rhs.purchaseValue
  }
  
  // 매수금액 = 매수평균가 * 보유량
  var purchaseValue: Double {
    return avgPrice * amount
  }
  
  // 평가금액 = 보유량 * 현재 가격
  var evaluationAmount: Double {
    return amount * crypto.currentPrice
  }
      
  // 평가손익 = 평가금액 - 매수금액
  var evaluationProfit: Double {
    return evaluationAmount - purchaseValue
  }
      
  // 평가손익률 = ((평가금액 - 매수금액) / 매수금액) * 100
  var evaluationRate: Double {
    return (evaluationProfit / purchaseValue) * 100
  }
}

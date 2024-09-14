struct Account: Decodable {
    let number: String // 계좌번호
    let currency: String // 화폐 단위
    let balance: Double // 주문가능 금액
    let locked: Double // 주문 중 묶여있는 금액
    let avgBuyPrice: Double // 매수 평균가
}

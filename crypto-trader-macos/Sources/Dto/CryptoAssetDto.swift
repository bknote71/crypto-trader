struct CryptoAssetDto: Decodable {
  let market: String
  let amount: Double // 보유량
  let avgPrice: Double
}

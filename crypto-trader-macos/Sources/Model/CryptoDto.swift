struct CryptoDto: Decodable {
  let market: String
  let nameKr: String
  let nameEn: String
  
  func toCrypto() -> Crypto {
    Crypto(code: market, nameKr: nameKr, nameEn: nameEn, ticker: Ticker())
  }
}

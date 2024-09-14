struct UserInfo: Decodable {
  let account: Account
  let assets: [CryptoAssetDto]
}

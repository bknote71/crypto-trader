struct User {
  let account: Account
  let assets: CryptoAssets
  
  init(account: Account, assets: [CryptoAsset]) {
    self.account = account
    self.assets = CryptoAssets(assets: assets)
  }
}

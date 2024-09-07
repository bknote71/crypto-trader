import Foundation

struct Crypto: Identifiable {
    let id = UUID()
    let nameKr: String
    let nameEn: String
    let tradingPrice: Double
    let change: Double
    let changeValue: Double
    let volume: String
}

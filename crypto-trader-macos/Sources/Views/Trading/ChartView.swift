import SwiftUI

struct ChartView: View {
  @EnvironmentObject private var cryptoViewModel: CryptoViewModel
  
  var body: some View {
    chartHeaderView
    divider
    chartMainView
  }
  
  var divider: some View {
    Color(.gray)
      .frame(height: 0.5)
      .padding(0)
  }
  
  var chartHeaderView: some View {
    Group {
      if let ticker = cryptoViewModel.crypto?.ticker {
        HStack {
          // 왼쪽 시세 정보
          VStack(alignment: .leading, spacing: 4) {
            Text("\(String(ticker.tradePrice)) KRW")
              .font(.title)
              .foregroundColor(.blue)
            HStack {
              Text("\(ticker.signedChangeRate.formattedPrice())%")
                .foregroundColor(.blue)
              Text("\(ticker.signedChangeRate < 0 ? "▼" : "▲") \(ticker.signedChangePrice.formattedPrice())")
                .foregroundColor(.blue)
            }
            .font(.subheadline)
          }
          Spacer()
          
          // 오른쪽 상세 정보
          HStack(alignment: .center, spacing: 12) {
            VStack(spacing: 8) {
              HStack {
                Text("고가")
                Spacer()
                Text("\(ticker.highPrice)")
                  .foregroundColor(.red)
              }
              divider
              HStack {
                Text("저가")
                Spacer()
                Text("\(ticker.highPrice)")
                  .foregroundColor(.blue)
              }
            }
            .frame(width: 160)
            
            VStack(spacing: 8) {
              HStack {
                Text("거래량(24h)")
                Spacer()
                Text(String(ticker.accTradeVolume24h))
              }
              divider
              HStack {
                Text("거래대금(24H)")
                Spacer()
                Text("\(ticker.accTradePrice24h) KRW")
              }
            }
            .frame(width: 200)
          }
          .font(.subheadline)
        }
        .padding(8)
        .background(.white)
      }
    }
  }
  
  var chartMainView: some View {
    CandleChartDetailView()
  }
}

#Preview {
    ChartView()
}

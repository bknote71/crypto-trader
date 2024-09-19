import DGCharts
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
  
  @EnvironmentObject var candleViewModel: CandleViewModel
  @ObservedObject var syncer = ChartXAxisSyncer()
  @State private var visibleCount: Double = 30
  @State private var chartRatio: CGFloat = 0.7
  @State private var sharedXRange: ClosedRange<Double> = 0...10
  
  var chartMainView: some View {
    GeometryReader { geo in
      VStack(spacing: 0) {
        CandleChartRepresentable(
          entries: $candleViewModel.candleEntries,
          crypto: $cryptoViewModel.crypto,
          visibleCount: $visibleCount,
          sharedXRange: $sharedXRange
        )
        .frame(height: geo.size.height * chartRatio)
        .clipped()
        
        Rectangle()
          .fill(Color.gray50)
          .frame(height: 2)
          .gesture(DragGesture()
            .onChanged { value in
              let dragAmount = value.translation.height / geo.size.height
              let newRatio = chartRatio + dragAmount
              chartRatio = max(0.1, min(0.9, newRatio))
            }
          )
        
        BarChartRepresentable(
          entries: $candleViewModel.barEntries,
          crypto: $cryptoViewModel.crypto,
          visibleCount: $visibleCount,
          sharedXRange: $sharedXRange,
          candleEntries: $candleViewModel.candleEntries
        )
          .frame(height: geo.size.height * (1 - chartRatio))
          .clipped()
      }
    }
  }
}

class ChartXAxisSyncer: ObservableObject {
  @Published var visibleXRange: (Double, Double) = (0, 30)
  
  func updateVisibleRange(newRange: (Double, Double)) {
    if newRange != visibleXRange {
      visibleXRange = newRange
    }
  }
}


//#Preview {
////    ChartView()
//}

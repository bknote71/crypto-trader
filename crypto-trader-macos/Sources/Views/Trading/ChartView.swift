import DGCharts
import SwiftUI

struct ChartView: View {
  @EnvironmentObject private var cryptoViewModel: CryptoViewModel
  
  @State private var lastMarket: String?
  @State private var dragLineColor: Color = .gray100
  @State private var isHovered: Bool = false
  
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
            VStack(spacing: 4) {
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
    .onReceive(cryptoViewModel.$crypto) { crypto in
      guard let crypto, crypto.market != lastMarket else { return }
      lastMarket = crypto.market
      candleViewModel.fetchAllCandles(market: crypto.market, unit: .one_minute)
//      candleViewModel.fetchCandleInfos(market: crypto.market, unit: .one_minute)
    }
  }
  
  @EnvironmentObject var candleViewModel: CandleViewModel
  @ObservedObject var syncer = ChartXAxisSyncer()
  @State private var visibleCount: Double = 30
  @State private var chartRatio: CGFloat = 0.7
  @State private var sharedXRange: ClosedRange<Double> = 0...30
  @State private var draggingId: String? = nil
  
  var chartMainView: some View {
    GeometryReader { geo in
      VStack(spacing: 0) {
        CandleChartRepresentable(
          entries: $candleViewModel.candleEntries,
          crypto: $cryptoViewModel.crypto,
          visibleCount: $visibleCount,
          sharedXRange: $sharedXRange,
          draggingId: $draggingId,
          candleViewModel: candleViewModel
        )
        .frame(height: geo.size.height * chartRatio)
        .clipped()
        
        Rectangle()
          .fill(dragLineColor)
          .frame(height: 4)
          .padding(.top, -11)
          .gesture(DragGesture()
            .onChanged { value in
              let dragAmount = value.translation.height / geo.size.height
              let newRatio = chartRatio + dragAmount
              chartRatio = max(0.1, min(0.9, newRatio))
            }
          )
        // Cursor change NOT WORKING
        // https://stackoverflow.com/questions/61984959/swiftui-system-cursor/67851290#67851290
          .onHover { isHovered in
            self.isHovered = isHovered
            if self.isHovered {
              dragLineColor = .gray200
              
              NSApp.windows.forEach { $0.disableCursorRects() }
              NSCursor.resizeUpDown.set() // 마우스가 호버되면 위아래로 늘릴 수 있는 커서 모양으로 변경
            } else {
              dragLineColor = .gray100
              
              NSCursor.arrow.pop() // 호버가 끝나면 기본 커서로 변경
              NSApp.windows[0].enableCursorRects()
            }
          }
        
        BarChartRepresentable(
          barEntries: $candleViewModel.barEntries,
          candleEntries: $candleViewModel.candleEntries,
          crypto: $cryptoViewModel.crypto,
          visibleCount: $visibleCount,
          sharedXRange: $sharedXRange,
          draggingId: $draggingId
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

import Combine
import DGCharts
import Foundation

class CandleViewModel: ObservableObject {
  
  @Published var items: [Candle]
  
  private let encoder = JSONEncoder()
  private var cancellableBag = Set<AnyCancellable>()
  
  private let candleWebSocketManager = JsonWebSocketManager<Candle>()
  
  init() {
    self.items = Self.dummyData()
    // TODO: 1. fetch all candle data(KRW-BTC)
  }
  
  public func fetchCandle(market: String, unit: CandleUnit) {
    // unit: ONEMINUTE, FIVEMINUTE
    if !candleWebSocketManager.isConnected {
      let candleUrl = "ws://127.0.0.1:8090/candle"
      candleWebSocketManager.connect(url: candleUrl)
        .receive(on: RunLoop.main)
        .sink { [weak self] candle in
          guard let self else { return }
          
          let adjustment: Double = candle.open == candle.close ? 100: 0
          print("adjust \(adjustment)")
          
          items.append(candle)
        }
        .store(in: &cancellableBag)
    }
    
    let dto = CandleRequestDto(market: market, unit: unit)
    do {
      let data = try encoder.encode(dto)
      guard let message = String(data: data, encoding: .utf8) else {
        // TODO: throw err
        return
      }
      
      candleWebSocketManager.sendMessage(message)
    } catch {
      print("err: \(error)")
    }
  }
  
  static func dummyData() -> [Candle] {
    var dummy = [Candle]()
    var previousClose: Double = 100.0
    
    for i in 0..<100 {
      let high = previousClose + Double.random(in: 0...30)
      let low = previousClose - Double.random(in: 0...30)
      let close = Double.random(in: low...high)
      let open = previousClose // open은 이전 close 값
      
      // 새로운 CandleChartDataEntry 생성
      let entry = Candle(open: open, close: close, high: high, low: low, time: Date.now + TimeInterval(10*i))
      
      // 엔트리를 배열에 추가
      dummy.append(entry)
      
      // 현재 close 값을 다음 루프에서 사용할 수 있도록 저장
      previousClose = close
    }
    
    return dummy
  }
}

import Combine
import DGCharts
import Foundation
import SwiftProtobuf

class CandleViewModel: ObservableObject {
  
  @Published var candleEntries = [CandleChartDataEntry]()
  @Published var barEntries = [BarChartDataEntry]()
  
  private let jsonClient = JsonAPIClient()
  private let candleWSClient = BinaryWSClient()
  
  private let encoder = JSONEncoder()
  
  private var cancellableBag = Set<AnyCancellable>()
  
  private var currentMarket: String = "KRW-BTC"
  private var startDate: Double = 0
  private var left: Double = 0
  
  init() {}
  
  // Public
  
  public func fetchCandleInfos(market: String, unit: CandleUnit) {
    guard let candlesInfo = APIEndpoint.candlesInfo.url else { return }
    
    let startTime = Date()
    
    jsonClient.request(url: candlesInfo, param: ["market": market,  "unit": CandleUnit.one_minute.rawValue])
      .filter { (_, response) in response.statusCode < 300 }
      .compactMap(\.0)
      .receive(on: RunLoop.main)
      .sink { completion in
        switch completion {
        case .finished:
          print("fetch completed \(Date().timeIntervalSince(startTime) * 1000)")
          break
        case .failure(let error):
          print("Failed with error: \(error)")
        }
      } receiveValue: { [weak self] (candlesInfo: CandlesInfo) in
        guard let self else { return }
        
        candleEntries = []
        barEntries = []
        
        currentMarket = market
        startDate = candlesInfo.startDate
        left = 0
        
        for i in 0..<candlesInfo.count {
          let newCandleEntry = CandleChartDataEntry(x: startDate + Double(i),
                                              shadowH: 0,
                                              shadowL: 0,
                                              open: 0,
                                              close: 0)
          
          let newBarEntry = BarChartDataEntry(x: startDate + Double(i),
                                              y: 0)
          
          candleEntries.append(newCandleEntry)
          barEntries.append(newBarEntry)
        }
        
        candlesInfo
          .candles
          .compactMap { try? PCandle(serializedBytes: $0) }
          .enumerated()
          .forEach { [weak self] i, candle in
            guard let self else { return }
            
            let ri = candleEntries.count - 1 - i
            if ri < 0 { return }
            
            updateCandle(ri, candle)
          }
        
        // 3. connect
        connect()
        send(market: market, unit: unit)
      }
      .store(in: &cancellableBag)
  }
  
  public func fetchCandlesBasedOnX(_ x: Double) {
    guard x <= left + 15, let candlesUrl = APIEndpoint.candles.url else { return }
    
    let startDate = "\(left - 100)"
    let endDate = "\(left - 1)"
    
    jsonClient.request(url: candlesUrl, param: ["market": currentMarket, "unit": CandleUnit.one_minute.rawValue, "start": startDate, "end": endDate])
      .filter { (_, response) in response.statusCode < 300 }
      .compactMap(\.0)
      .receive(on: RunLoop.main)
      .sink { completion in
        switch completion {
        case .finished:
          break
        case .failure(let error):
          print("Failed with error: \(error)")
        }
      } receiveValue: { [weak self] (datas: [Data]) in
        guard let self, let index = candleEntries.firstIndex(where: {self.left <= $0.x && $0.x < self.left + 1}) else { return }
        
        datas
          .compactMap { try? PCandle(serializedBytes: $0) }
          .enumerated()
          .forEach { [weak self] i, candle in
            guard let self else { return }
            
            let ri = index - 1 - i
            if ri < 0 { return }
            
            updateCandle(ri, candle)
          }
      }
      .store(in: &cancellableBag)
  }
  
  public func fetchAllCandles(market: String, unit: CandleUnit) {
    guard let allCandlesUrl = APIEndpoint.allCandles.url else { return }
    
    let startTime = Date()
    
    jsonClient.request(url: allCandlesUrl, param: ["market": market, "unit": CandleUnit.one_minute.rawValue ])
      .filter { (_, response) in response.statusCode < 300 }
      .compactMap(\.0)
      .receive(on: RunLoop.main)
      .sink { completion in
        switch completion {
        case .finished:
          print("fetch completed \(Date().timeIntervalSince(startTime) * 1000)")
          break
        case .failure(let error):
          print("Failed with error: \(error)")
        }
      } receiveValue: { [weak self] (datas: [Data]) in
        guard let self else { return }
        
        let candles = datas
          .compactMap { try? PCandle(serializedBytes: $0) }
          .compactMap { (pcandle) -> Candle? in
            return Candle(open: pcandle.open,
                          close: pcandle.close,
                          high: pcandle.high,
                          low: pcandle.low,
                          time: Date(),
                          volume: pcandle.volume)
          }
        
        print("fetch candles count \(datas.count) \(candles.count)")
        
        candleEntries = []
        barEntries = []
        
        candles.forEach { self.appendCandle($0) }
        
        connect()
        send(market: market, unit: unit)
      }
      .store(in: &cancellableBag)
  }
 
  // Private
  
  private func appendCandle(_ candle: Candle) {
    let newCandleEntry = CandleChartDataEntry(x: startDate + Double(candleEntries.count),
                                        shadowH: candle.high,
                                        shadowL: candle.low,
                                        open: candle.open,
                                        close: candle.close)

    let newBarEntry = BarChartDataEntry(x: startDate + Double(candleEntries.count),
                                        y: min(1000, max(candle.volume * 1000, 5)))
    
    candleEntries.append(newCandleEntry)
    barEntries.append(newBarEntry)
  }
  
  private func updateCandle(_ i: Int, _ candle: PCandle) {
    candleEntries[i] = CandleChartDataEntry(x: candleEntries[i].x,
                                        shadowH: candle.high,
                                        shadowL: candle.low,
                                        open: candle.open,
                                        close: candle.close)

    left = candleEntries[i].x
  }
}

// MARK: - WebSocket

extension CandleViewModel {
  private func connect() {
    guard  !candleWSClient.isConnected,
           let candleUrl = WSEndpoint.candle.url
    else { return }
    
    candleWSClient.connect(url: candleUrl)
      .receive(on: RunLoop.main)
      .compactMap{ try? PCandle(serializedBytes: $0) }
      .compactMap { (pcandle) -> Candle? in
        
        return Candle(open: pcandle.open,
                      close: pcandle.close,
                      high: pcandle.high,
                      low: pcandle.low,
                      time: Date(),
                      volume: pcandle.volume)
      }
      .sink { [weak self] candle in
        guard let self else { return }
        appendCandle(candle)
      }
      .store(in: &cancellableBag)
  }
  
  private func send(market: String, unit: CandleUnit) {
    let dto = CandleRequestDto(market: market, unit: unit)
    do {
      let data = try encoder.encode(dto)
      guard let message = String(data: data, encoding: .utf8) else {
        // TODO: throw err
        return
      }
      
      candleWSClient.sendMessage(message)
    } catch {
      print("err: \(error)")
    }
  }
}


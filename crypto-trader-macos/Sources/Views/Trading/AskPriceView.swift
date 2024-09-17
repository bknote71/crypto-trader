//
//  AskPriceView.swift
//  crypto-trader-macos
//
//  Created by bknote71 on 9/8/24.
//

import SwiftUI

enum AskPriceItem {
  case general
  case cumulative
  
  var title: String {
    switch self {
    case .general:
      "일반호가"
    case .cumulative:
      "누적호가"
    }
  }
}

struct AskPriceView: View {
  @State private var selectedItem: AskPriceItem = .general
      
  var body: some View {
    VStack(spacing: 0) {
      // 메뉴 탭
      HStack(spacing: 0) {
        TabButton(title: "일반호가", isSelected: selectedItem == .general, width: 100, spacing: 9) {
            // TODO
        }
        
        TabButton(title: "누적호가", isSelected: selectedItem == .cumulative, width: 100, spacing: 9) {
            // TODO
        }
        Spacer()
      }
      
      switch selectedItem {
      case .general:
        GeneralAskPriceView()
      case .cumulative:
        Text("누적호가")
      }
    }
    .frame(width: AskPriceViewConst.width, height: AskPriceViewConst.height)
    .background(.white)
  }
}

struct GeneralAskPriceView: View {
    let blueRows = [
        (rate: "0.092", price: "73,781,000", percent: "+0.19%"),
        (rate: "0.010", price: "73,780,000", percent: "+0.19%"),
        (rate: "0.000", price: "73,774,000", percent: "+0.18%"),
        (rate: "0.092", price: "73,781,000", percent: "+0.19%"),
    ]
    
    let redRows = [
        (price: "73,733,000", percent: "+0.13%", rate: "0.065"),
        (price: "73,732,000", percent: "+0.12%", rate: "0.133"),
        (price: "73,731,000", percent: "+0.12%", rate: "0.228"),
        (price: "73,731,000", percent: "+0.12%", rate: "0.228"),
    ]
    
  
  var body: some View {
    VStack(spacing: 0) {
      // 파란색 리스트
      HStack(alignment: .bottom, spacing: 0) {
        VStack(spacing: 0) {
          ForEach(blueRows, id: \.price) { row in
            HStack(spacing: 0) {
              Text(row.rate)
                .padding(.trailing, 12)
                .frame(width: 160, alignment: .trailing)
              
              Color(.white)
                .frame(width: 1)
              
              HStack(spacing: 0) {
                Text(row.price)
                  .padding(.trailing, 20)
                Text(row.percent)
                  .foregroundColor(.blue)
              }
              .frame(width: 170)
            }
            .frame(height: 39)
            .background(Color.blue.opacity(0.05))
            .overlay(
               Rectangle()
                .frame(height: 1)
                .foregroundColor(.white),
               alignment: .bottom
            )
          }
        }
        
        // 오른쪽 정보
        VStack(alignment: .leading, spacing: 8) {
          Text("거래량: 1,479 BTC")
          Text("거래대금: 109 백만원")
          Text("52주 최고: 105,000")
          Text("52주 최저: 34,100")
        }
        .padding(.bottom, 12)
        .padding(.horizontal, 12)
        .frame(width: 160)
      }
      
      
      Divider()
      
      // 빨간색 리스트
      HStack(alignment: .top, spacing: 0) {
        // 체결 강도
        VStack {
          Text("체결강도: +132.85%")
        }
        .padding(.top, 12)
        .padding(.horizontal, 12)
        .frame(width: 160)
        
        VStack(spacing: 0) {
          ForEach(redRows, id: \.price) { row in
            HStack(spacing: 0) {
              HStack(spacing: 0) {
                Text(row.price)
                  .padding(.trailing, 20)
                Text(row.percent)
                  .foregroundColor(.red)
              }
              .frame(width: 170)
              
              Color(.white)
                .frame(width: 1)
              
              Text(row.rate)
                .padding(.leading, 12)
                .frame(width: 160, alignment: .leading)
            }
            .frame(height: 39)
            .background(Color.red.opacity(0.05))
            .overlay(
               Rectangle()
                .frame(height: 1)
                .foregroundColor(.white),
               alignment: .top
            )
          }
        }
      }
    }
    .padding(0)
  }
}


#Preview {
    AskPriceView()
}

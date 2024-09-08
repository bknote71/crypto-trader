//
//  AskPriceView.swift
//  crypto-trader-macos
//
//  Created by bknote71 on 9/8/24.
//

import SwiftUI

struct AskPriceView: View {
  @State private var selectedTab: String = "일반호가"
      
  var body: some View {
    VStack(spacing: 0) {
      // 메뉴 탭
      HStack(spacing: 0) {
        tabButton(title: "일반호가", selectedTab: $selectedTab)
        tabButton(title: "누적호가", selectedTab: $selectedTab)
        tabButton(title: "호가주문", selectedTab: $selectedTab)
        Spacer()
      }
      
      // 선택된 탭에 따른 뷰
      if selectedTab == "일반호가" {
        GeneralAskPriceView()
      }
      // 다른 탭에 대한 뷰는 필요 시 추가
    }
    .frame(width: 490, height: 360)
    .background(.white)
  }
  
  func tabButton(title: String, selectedTab: Binding<String>) -> some View {
    let isSelected = selectedTab.wrappedValue == title
    return VStack(spacing: 12) {
      Text(title)
        .foregroundColor(isSelected ? .blue : .black)
        .padding(.top, 12)
      Rectangle()
        .frame(height: 3)
        .foregroundColor(isSelected ? .blue : .clear)
    }
    .contentShape(Rectangle())
    .onTapGesture {
      selectedTab.wrappedValue = title
    }
    .frame(width: 100)
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

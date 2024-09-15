import Charts
import SwiftUI

struct PortfolioView: View {
  @EnvironmentObject var userViewModel: UserViewModel
  
  @State private var selectedTab = "보유자산"
  @State private var animate: Bool = false
  
  var body: some View {
    VStack(spacing: 0) {
      HStack {
        tabBar
        Spacer()
      }
      Color(Color.gray100)
        .frame(height: 1)
      
      if selectedTab == "보유자산" {
        summaryView
        Divider()
        assetListView
      }
      Spacer()
    }
    .frame(width: 950)
    .background(.white)
    .onAppear {
      userViewModel.fetchUserInfo()
    }
  }
  
  var tabBar: some View {
    HStack(spacing: 0) {
      tabButton(title: "보유자산", isSelected: selectedTab == "보유자산") {
        selectedTab = "보유자산"
      }
      tabButton(title: "거래내역", isSelected: selectedTab == "거래내역") {
        selectedTab = "거래내역"
      }
      tabButton(title: "미체결", isSelected: selectedTab == "미체결") {
        selectedTab = "미체결"
      }
    }
  }
  
  func tabButton(title: String, isSelected: Bool, action: @escaping () -> Void) -> some View {
    VStack(alignment: .center, spacing: 9) {
      Text(title)
        .font(.title3)
        .foregroundColor(isSelected ? .blue : .black)
        .padding(.top, 12)
      Rectangle()
        .frame(height: 3)
        .foregroundColor(isSelected ? .blue : .clear)
    }
    .frame(width: 150)
    .contentShape(Rectangle())
    .onTapGesture {
      action()
    }
  }
  
  var summaryView: some View {
    HStack(spacing: 0) {
      if let account = userViewModel.user?.account {
        // account
        VStack(spacing: 0) {
          HStack(spacing: 0) {
            HStack(spacing: 0) {
              Text("보유 KRW")
              Spacer()
              Text("\(account.holdCash.formattedPrice()) KRW")
            }
            .padding(.trailing, 24)
            
            HStack(spacing: 0) {
              Text("총보유자산")
              Spacer()
              Text("\(account.totalAsset.formattedPrice()) KRW")
            }
          }
          .padding(.bottom, 24)
          
          Divider()
          
          HStack(alignment: .top, spacing: 0) {
            VStack(spacing: 24) {
              HStack(spacing: 0) {
                Text("총 매수")
                Spacer()
                Text("\(account.totalBidAmount.formattedPrice()) KRW")
              }
              
              HStack(spacing: 0) {
                Text("총 평가")
                Spacer()
                Text("\(account.avgBidPrice.formattedPrice()) KRW")
              }
              
              HStack(spacing: 0) {
                Text("주문가능(baalnce)")
                Spacer()
                Text("\(account.balance.formattedPrice()) KRW")
              }
            }
            .padding(.trailing, 24)
            
            VStack(spacing: 24) {
              HStack(spacing: 0) {
                Text("총평가손익")
                Spacer()
                Text("\(account.totalProfitLoss.formattedPrice()) KRW")
              }
              
              HStack(spacing: 0) {
                Text("총평가수익률")
                Spacer()
                Text("\(account.totalProfitLossRate.formattedPrice()) %")
              }
            }
          }
          .padding(.vertical, 24)
          Spacer()
        }
        .padding(24)
        
        Color(Color.gray150)
          .frame(width: 1)
        
        circleChart
          .padding(.horizontal, 16)
      }
    }
    .frame( height: 240)
  }
  
  var circleChart: some View {
    Group {
      if let assets = userViewModel.user?.assets {
        HStack(alignment: .center, spacing: 0) {
          // legend (foreeach)
          VStack {
            ForEach(Array(assets.cryptoAssets.allElements().enumerated()), id: \.element.crypto.market) { index, asset in
              HStack(spacing: 0) {
                Circle()
                  .fill(donutColor(index))
                  .frame(width: 10, height: 10)
                  .padding(.trailing, 8)
                Text(asset.crypto.market)
                Spacer()
                Text("\(assets.calculateRate(for: asset).formattedPrice(min: 1, max: 1)) %")
              }
              .frame(width: 140)
            }
          }
          .padding(.trailing, 24)
          
          // real chart content
          ZStack {
            ForEach(Array(assets.cryptoAssets.allElements().enumerated()), id: \.element.crypto.market) { index, asset in
              let startAngle = Angle(degrees: assets.degrees(for: index))
              let endAngle = Angle(degrees: assets.degrees(for: index + 1))
              DonutSlice(
                startAngle: startAngle,
                endAngle: animate ? endAngle : startAngle
              )
              .fill(donutColor(index))
              .rotationEffect(.degrees(animate ? -90: -90 - startAngle.degrees / 4), anchor: .center)
              .animation(.easeOut(duration: 1.0), value: animate)
            }
            Text("보유 비중\n(%)")
              .multilineTextAlignment(.center)
          }
          .frame(width: 160, height: 160)
          .onAppear {
            withAnimation {
              animate = true
            }
          }
        }
      } else {
        HStack {
          Text("유저가 없어... 로그인이 필요... 해...")
        }
      }
    }
  }
  
  
  let colors: [Color] = [.purple, Color.gray300, .brown, .indigo] // 초록, 보라, 회색, 와인
  
  func donutColor(_ index: Int) -> Color {
    return colors[index % colors.count]
  }
  
  let headerColumns: [GridItem] = [
    GridItem(.flexible(), spacing: 0, alignment: .center),
    GridItem(.flexible(), spacing: 0, alignment: .center),
    GridItem(.flexible(), spacing: 0, alignment: .center),
    GridItem(.flexible(), spacing: 0, alignment: .center),
    GridItem(.flexible(), spacing: 0, alignment: .center),
    GridItem(.flexible(), spacing: 0, alignment: .center),
  ]
      
  let dataColumns: [GridItem] = [
    GridItem(.flexible(), spacing: 0, alignment: .leading),  // 보유 자산 컬럼
    GridItem(.flexible(), spacing: 0, alignment: .trailing), // 보유수량
    GridItem(.flexible(), spacing: 0, alignment: .trailing), // 매수 평균가
    GridItem(.flexible(), spacing: 0, alignment: .trailing), // 매수금액
    GridItem(.flexible(), spacing: 0, alignment: .trailing), // 평가금액
    GridItem(.flexible(), spacing: 0, alignment: .trailing), // 평가손익
  ]
  
  var assetListView: some View {
    VStack(alignment: .leading, spacing: 0) {
      Text("보유자산 목록")
        .font(.title2)
        .padding(.leading, 18)
        .padding(.vertical, 6)
      
      Divider()
      
      
      // 컬럼 헤더
      LazyVGrid(columns: headerColumns, spacing: 0) {
        Text("보유 자산").bold()
        Text("보유수량").bold()
        Text("매수 평균가").bold()
        Text("매수금액").bold()
        Text("평가금액")
          .bold()
          .frame(height: 25)
          .frame(maxWidth: .infinity)
          .background(Color.gray50)
        Text("평가손익").bold()
      }
      .frame(height: 25)
      .padding(.horizontal, 16)
      
      Divider()
      
      // 데이터 리스트
      if let assets = userViewModel.user?.assets {
        ForEach(assets.cryptoAssets.allElements()) { asset in
          LazyVGrid(columns: dataColumns, spacing: 0) {
            HStack(spacing: 0) {
              Image(systemName: "circle") // 이미지를 해당 심볼로 표시, 필요시 대체 가능
                .frame(width: 20, height: 20)
              Text(asset.crypto.market)
                .font(.headline)
            }
            .padding(4)
            
            Text(asset.amount.formattedPrice())
              .lineLimit(1)
              .padding(4)
            
            Text(asset.avgPrice.formattedPrice())
              .padding(4)
            
            Text(asset.purchaseValue.formattedPrice())
              .padding(4)
            
            Text(asset.evaluationAmount.formattedPrice())
              .lineLimit(1)
              .padding(4)
              .frame(height: 50)
              .frame(maxWidth: .infinity, alignment: .trailing)
              .background(Color.gray50)
            
            VStack(alignment: .trailing, spacing: 0) { // 평가손익 컬럼
              Text("\(asset.evaluationRate.formattedPrice(min: 1, max: 1))%")
              Text("\(asset.evaluationProfit.formattedPrice()) KRW")
            }
            .frame(maxWidth: .infinity, alignment: .trailing)
            .padding(4)
          
          }
          .frame(height: 50)
          .padding(.horizontal, 16)
          
          Color(Color.gray100)
            .frame(height: 1)
        }
      } else {
        Text("보유 자산이 없습니다.")
      }
    }
  }
  
  var columnDivider: some View {
    Color(Color.gray100)
      .frame(width: 1)
  }
}

#Preview {
    PortfolioView()
}

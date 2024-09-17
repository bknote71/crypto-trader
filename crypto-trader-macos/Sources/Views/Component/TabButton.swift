import SwiftUI

struct TabButton: View {
  let title: String
  let isSelected: Bool
  var selectedColor: Color = .blue
  let width: CGFloat
  let spacing: CGFloat
  let action: () -> Void
  
  var body: some View {
    VStack(alignment: .center, spacing: spacing) {
      Text(title)
        .font(.title3)
        .foregroundColor(isSelected ? selectedColor : .black)
        .padding(.top, spacing + 3)
      Rectangle()
        .frame(height: 3)
        .foregroundColor(isSelected ? selectedColor : .clear)
    }
    .frame(width: width)
    .contentShape(Rectangle())
    .onTapGesture {
      action()
    }
  }
}

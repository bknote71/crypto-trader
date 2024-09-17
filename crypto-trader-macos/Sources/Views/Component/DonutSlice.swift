import SwiftUI

struct DonutSlice: Shape {
  var startAngle: Angle
  var endAngle: Angle
  var innerRadiusRatio: CGFloat = 0.4 // 도넛의 두께 조절
  
  var animatableData: AnimatablePair<Double, Double> {
    get {
      AnimatablePair(startAngle.degrees, endAngle.degrees)
    }
    set {
      startAngle = Angle(degrees: newValue.first)
      endAngle = Angle(degrees: newValue.second)
    }
  }
  
  func path(in rect: CGRect) -> Path {
    var path = Path()
    let center = CGPoint(x: rect.midX, y: rect.midY)
    let radius = min(rect.width, rect.height) / 2
    let innerRadius = radius * innerRadiusRatio
    
    path.addArc(center: center, radius: radius, startAngle: startAngle, endAngle: endAngle, clockwise: false)
    path.addLine(to: CGPoint(
      x: center.x + innerRadius * cos(CGFloat(endAngle.radians)),
      y: center.y + innerRadius * sin(CGFloat(endAngle.radians))
    ))
    path.addArc(center: center, radius: innerRadius, startAngle: endAngle, endAngle: startAngle, clockwise: true)
    path.closeSubpath()
    
    return path
  }
}

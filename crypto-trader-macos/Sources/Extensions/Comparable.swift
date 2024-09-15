extension Comparable {
    /// 값을 지정된 범위로 클램프하는 메서드
  func clamped(to limits: ClosedRange<Self>) -> Self {
    return min(max(self, limits.lowerBound), limits.upperBound)
  }
}

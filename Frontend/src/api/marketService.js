import axiosMarket from "./axiosMarket";

export async function getCandles({
  symbols = "AAPL,MSFT",
  timeframe = "1Day",
  feed = "iex",
  start,
  end,
  lastDays = 60,
} = {}) {
  const params = { symbols, timeframe, feed, lastDays };
  if (start) params.start = start;
  if (end) params.end = end;

  const res = await axiosMarket.get("/candles", { params });
  return res.data || {};
}

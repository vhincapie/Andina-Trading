import { useEffect, useState } from "react";
import { obtenerSaldo } from "../../api/cuentasService";
import ErrorAlert from "../alerts/ErrorAlert";
import SuccessAlert from "../alerts/SuccessAlert";

export default function SaldoCard({ onlyBuyingPower = false }) {
  const [saldo, setSaldo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [aviso, setAviso] = useState("");
  const [error, setError] = useState("");

  const load = async () => {
    try {
      const { data, notice } = await obtenerSaldo();
      setSaldo(data || null);
      setAviso(notice || "");
      setError("");
    } catch (e) {
      const msg =
        e?.response?.data?.message ||
        e?.response?.data?.error ||
        "No se pudo obtener el saldo.";
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
    const interval = setInterval(load, 30000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (!error && !aviso) return;
    const t = setTimeout(() => {
      setError("");
      setAviso("");
    }, 7000);
    return () => clearTimeout(t);
  }, [error, aviso]);

  const buyingPower = saldo?.buying_power ?? saldo?.buyingPower ?? "0";

  const cardBase =
    "bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur-md p-6 text-slate-100";
  const label = "text-xs uppercase tracking-wide text-slate-400";
  const value = "text-2xl font-semibold text-emerald-300";
  const subCard =
    "rounded-xl border border-slate-800/80 bg-slate-900/60 p-4 hover:border-emerald-400/40 transition-colors duration-200";
  const gradientTitle =
    "text-lg font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent flex items-center gap-3";

  if (onlyBuyingPower) {
    return (
      <div className={`${cardBase} max-w-md mx-auto`}>
        <div className="flex items-center justify-between">
          <h3 className="font-semibold text-2xl text-slate-300">Saldo disponible</h3>
          {loading ? (
            <span className="text-slate-400 text-sm">Cargando...</span>
          ) : (
            <div className="ml-3 text-2xl font-semibold text-emerald-400">
              ${buyingPower}
            </div>
          )}
        </div>

        <div className="mt-4 space-y-3">
          <ErrorAlert message={error} onClose={() => setError("")} />
          <SuccessAlert message={aviso} onClose={() => setAviso("")} />
        </div>
      </div>
    );
  }

  const equity = saldo?.equity ?? saldo?.Equity ?? "0";
  const cash = saldo?.cash ?? saldo?.Cash ?? "0";

  return (
    <div className={`${cardBase} max-w-3xl mx-auto`}>
      <div className="flex items-center justify-between mb-4">
        <h3 className={gradientTitle}>
          <span>Mi saldo</span>
        </h3>
        {!loading && <span className={`${value} ml-2`}>${equity}</span>}
      </div>

      <ErrorAlert message={error} onClose={() => setError("")} />
      <SuccessAlert message={aviso} onClose={() => setAviso("")} />

      {loading ? (
        <p className="text-slate-400 mt-3">Cargando...</p>
      ) : (
        <div className="grid gap-4 sm:grid-cols-3 mt-3">
          <div className={subCard}>
            <div className={label}>Equidad (equity)</div>
            <div className={value}>${equity}</div>
          </div>
          <div className={subCard}>
            <div className={label}>Efectivo (cash)</div>
            <div className={value}>${cash}</div>
          </div>
          <div className={subCard}>
            <div className={label}>Buying Power</div>
            <div className={value}>${buyingPower}</div>
          </div>
        </div>
      )}
    </div>
  );
}

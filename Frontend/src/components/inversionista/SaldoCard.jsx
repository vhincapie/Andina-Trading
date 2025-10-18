import { useEffect, useState } from "react";
import { obtenerSaldo } from "../../api/cuentasService";
import ErrorAlert from "../alerts/ErrorAlert";
import SuccessAlert from "../alerts/SuccessAlert";

export default function SaldoCard() {
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

  const equity = saldo?.equity ?? saldo?.Equity ?? "0";
  const cash = saldo?.cash ?? saldo?.Cash ?? "0";
  const buyingPower = saldo?.buying_power ?? saldo?.buyingPower ?? "0";

  return (
    <div className="bg-white border rounded p-4 space-y-3">
      <h3 className="font-semibold text-lg">Mi saldo</h3>

      <ErrorAlert message={error} onClose={() => setError("")} />
      <SuccessAlert message={aviso} onClose={() => setAviso("")} />

      {loading ? (
        <p className="text-gray-600">Cargando...</p>
      ) : (
        <div className="grid gap-3 md:grid-cols-3">
          <div className="border rounded p-3">
            <div className="text-sm text-gray-600">Equidad (equity)</div>
            <div className="text-xl font-semibold">${equity}</div>
          </div>
          <div className="border rounded p-3">
            <div className="text-sm text-gray-600">Efectivo (cash)</div>
            <div className="text-xl font-semibold">${cash}</div>
          </div>
          <div className="border rounded p-3">
            <div className="text-sm text-gray-600">Buying Power</div>
            <div className="text-xl font-semibold">${buyingPower}</div>
          </div>
        </div>
      )}
    </div>
  );
}

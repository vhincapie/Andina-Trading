import { useState, useEffect } from "react";
import { crearTransferencia } from "../../api/cuentasService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";
import Modal from "../../components/Modal";

export default function RecargaModal({ open, onClose }) {
  const [amount, setAmount] = useState("");
  const [loading, setLoading] = useState(false);
  const [ok, setOk] = useState("");
  const [err, setErr] = useState("");

  useEffect(() => {
    if (!err && !ok) return;
    const t = setTimeout(() => {
      setErr("");
      setOk("");
    }, 7000);
    return () => clearTimeout(t);
  }, [err, ok]);

  const onSubmit = async (e) => {
    e.preventDefault();
    setErr("");
    setOk("");

    const val = Number(amount);
    if (!Number.isFinite(val) || val <= 0) {
      setErr("Ingresa un monto mayor a 0.");
      return;
    }

    setLoading(true);
    try {
      const resp = await crearTransferencia(val);
      setOk(`Transferencia creada correctamente. ID: ${resp?.id || "—"}`);
      setAmount("");
    } catch (e) {
      const msg =
        e?.response?.data?.message || "No se pudo crear la transferencia.";
      setErr(msg);
    } finally {
      setLoading(false);
    }
  };

  const label = "block text-xs uppercase tracking-wide text-slate-400 mb-1";
  const input =
    "w-full bg-slate-900/60 border border-slate-700/70 rounded-lg px-3 py-2 text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-400/60 focus:border-emerald-400/40";
  const btn =
    "bg-emerald-600 hover:bg-emerald-500 text-white px-5 py-2.5 rounded-lg font-semibold transition-colors disabled:opacity-60";

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Recargar cuenta"
      footer={
        <div className="flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 rounded-lg border border-slate-700 bg-slate-900/50 text-slate-300 hover:bg-slate-800 transition-colors"
          >
            Cerrar
          </button>
        </div>
      }
    >
      <p className="text-slate-300 mb-4">
        Las recargas pueden demorar en reflejarse según el horario.
      </p>

      <ErrorAlert message={err} onClose={() => setErr("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      <form onSubmit={onSubmit} className="space-y-5 mt-4">
        <div>
          <label className={label}>Monto</label>
          <input
            className={input}
            inputMode="numeric"
            placeholder="25000"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            required
          />
          <p className="text-xs text-slate-500 mt-1">
            Ingresa el monto que deseas recargar (en COP).
          </p>
        </div>

        <div className="pt-2 flex justify-end">
          <button type="submit" className={btn} disabled={loading}>
            {loading ? "Procesando..." : "Recargar"}
          </button>
        </div>
      </form>
    </Modal>
  );
}

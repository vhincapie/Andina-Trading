import { useState, useEffect } from "react";
import { crearTransferencia } from "../../api/cuentasService";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";

export default function RecargaPage() {
  const [amount, setAmount] = useState("");
  const [loading, setLoading] = useState(false);
  const [ok, setOk] = useState("");
  const [err, setErr] = useState("");

  useEffect(() => {
    if (err) {
      const t = setTimeout(() => setErr(""), 7000);
      return () => clearTimeout(t);
    }
    if (ok) {
      const t = setTimeout(() => setOk(""), 7000);
      return () => clearTimeout(t);
    }
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

  return (
    <div className="max-w-md mx-auto space-y-4">
      <h2 className="text-2xl font-semibold">Recargar cuenta</h2>
      <p className="text-gray-600">
        Las recargas pueden demorar en reflejarse según el horario.
      </p>

      <ErrorAlert message={err} onClose={() => setErr("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      <form
        onSubmit={onSubmit}
        className="bg-white border rounded p-4 space-y-3"
      >
        <div>
          <label className="block text-sm mb-1">Monto</label>
          <input
            className="border p-2 rounded w-full"
            inputMode="numeric"
            placeholder="25000"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            required
          />
        </div>

        <button
          type="submit"
          className="bg-blue-600 text-white px-3 py-2 rounded disabled:opacity-60"
          disabled={loading}
        >
          {loading ? "Procesando..." : "Recargar"}
        </button>
      </form>
    </div>
  );
}

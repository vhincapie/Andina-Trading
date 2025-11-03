import { useState } from "react";
import RecargaModal from "../../pages/inversionista/RecargaModal";

export default function RelacionesACHList({ items = [] }) {
  const [showRecarga, setShowRecarga] = useState(false);

  if (!Array.isArray(items) || items.length === 0) {
    return (
      <div className="bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur p-6 text-center text-slate-400">
        Aún no tienes cuentas asociadas.
      </div>
    );
  }

  const mapTipo = (t) => {
    const v = String(t || "").toUpperCase();
    if (v === "CHECKING") return "Corriente";
    if (v === "SAVINGS") return "Ahorros";
    return v || "—";
  };

  const btnPrimary =
    "inline-block bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-2 rounded-lg text-sm font-bold transition-colors shadow-md focus:outline-none focus:ring-2 focus:ring-emerald-400/50";

  return (
    <>
      <div className="hidden md:block overflow-x-auto">
        <div className="bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur">
          <table className="min-w-full text-sm">
            <thead className="bg-slate-900/70 text-slate-300 border-b border-slate-800">
              <tr>
                <th className="py-3 px-4 text-left font-medium">Titular</th>
                <th className="py-3 px-4 text-left font-medium">Banco</th>
                <th className="py-3 px-4 text-left font-medium">Número</th>
                <th className="py-3 px-4 text-left font-medium">Tipo</th>
                <th className="py-3 px-4 text-left font-medium">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800">
              {items.map((c) => (
                <tr
                  key={c.id || `${c.nickname}-${c.bankAccountNumber}`}
                  className="text-slate-200 hover:bg-slate-800/40 transition-colors"
                >
                  <td className="py-3 px-4">
                    {c.accountOwnerName || c.account_owner_name}
                  </td>
                  <td className="py-3 px-4">{c.nickname}</td>
                  <td className="py-3 px-4">
                    ****{String(c.bankAccountNumber || c.bank_account_number || "").slice(-4)}
                  </td>
                  <td className="py-3 px-4">
                    {mapTipo(c.bankAccountType || c.bank_account_type)}
                  </td>
                  <td className="py-3 px-4">
                    <button
                      onClick={() => setShowRecarga(true)}
                      className={btnPrimary}
                    >
                      Recargar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
      <div className="md:hidden space-y-3">
        {items.map((c) => (
          <div
            key={c.id || `${c.nickname}-${c.bankAccountNumber}`}
            className="bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur p-4"
          >
            <p className="text-sm text-slate-200">
              <span className="text-slate-400">Titular:</span>{" "}
              {c.accountOwnerName || c.account_owner_name}
            </p>
            <p className="text-sm text-slate-200">
              <span className="text-slate-400">Banco:</span> {c.nickname}
            </p>
            <p className="text-sm text-slate-200">
              <span className="text-slate-400">Número:</span> ****
              {String(c.bankAccountNumber || c.bank_account_number || "").slice(-4)}
            </p>
            <p className="text-sm text-slate-200">
              <span className="text-slate-400">Tipo:</span>{" "}
              {mapTipo(c.bankAccountType || c.bank_account_type)}
            </p>
            <div className="mt-3">
              <button
                onClick={() => setShowRecarga(true)}
                className={btnPrimary}
              >
                Recargar
              </button>
            </div>
          </div>
        ))}
      </div>
      <RecargaModal open={showRecarga} onClose={() => setShowRecarga(false)} />
    </>
  );
}

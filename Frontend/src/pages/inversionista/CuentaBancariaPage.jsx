import { useState, useEffect, useMemo } from "react";
import {
  crearRelacionACH,
  obtenerRelacionesACH,
} from "../../api/cuentasService";
import RelacionesACHList from "../../components/inversionista/RelacionesACHList";
import ErrorAlert from "../../components/alerts/ErrorAlert";
import SuccessAlert from "../../components/alerts/SuccessAlert";
import { bancosPorPais } from "../../data/bancosPorPais";

export default function CuentaBancariaPage() {
  const [pais, setPais] = useState("CO");
  const [nickname, setNickname] = useState("");
  const [tipoCuenta, setTipoCuenta] = useState("CHECKING");
  const [numeroCuenta, setNumeroCuenta] = useState("");
  const [titular, setTitular] = useState("");
  const [list, setList] = useState([]);
  const [error, setError] = useState("");
  const [ok, setOk] = useState("");
  const [loading, setLoading] = useState(true);

  const yaTieneCuenta = useMemo(
    () => Array.isArray(list) && list.length > 0,
    [list]
  );
  const bancos = useMemo(() => bancosPorPais[pais] || [], [pais]);

  const traducirMensaje = (msg) => {
    if (!msg) return "";
    return msg
      .replace(/accountOwnerName/gi, "Nombre del titular")
      .replace(/nickname/gi, "Banco o alias")
      .replace(/bankAccountNumber/gi, "Número de cuenta")
      .replace(/bankAccountType/gi, "Tipo de cuenta")
      .replace(/country/gi, "País");
  };

  const getErrMsg = (err) => {
    const msg =
      err?.response?.data?.message ||
      err?.response?.data?.error ||
      (Array.isArray(err?.response?.data?.errors) &&
        err.response.data.errors[0]?.message) ||
      (typeof err?.response?.data === "string" ? err.response.data : "") ||
      err?.message ||
      "Ocurrió un error";
    return traducirMensaje(msg);
  };

  const loadList = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await obtenerRelacionesACH();
      setList(Array.isArray(data) ? data : []);
    } catch (err) {
      setError(
        getErrMsg(err) || "No se pudieron cargar las cuentas asociadas."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadList();
  }, []);

  useEffect(() => {
    if (error) {
      const t = setTimeout(() => setError(""), 7000);
      return () => clearTimeout(t);
    }
    if (ok) {
      const t = setTimeout(() => setOk(""), 7000);
      return () => clearTimeout(t);
    }
  }, [error, ok]);

  const onChangeNumero = (e) => {
    const soloDigitos = e.target.value.replace(/\D+/g, "").slice(0, 8);
    setNumeroCuenta(soloDigitos);
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");
    if (!/^\d{8}$/.test(numeroCuenta)) {
      setError("El número de cuenta debe tener exactamente 8 dígitos.");
      return;
    }
    try {
      await crearRelacionACH({
        account_owner_name: titular,
        bank_account_type: tipoCuenta,
        bank_account_number: numeroCuenta,
        nickname,
        country: pais,
      });
      setOk("Cuenta bancaria asociada correctamente.");
      setTitular("");
      setNumeroCuenta("");
      setNickname("");
      await loadList();
    } catch (err) {
      setError(getErrMsg(err) || "No se pudo asociar la cuenta bancaria.");
    }
  };

  const shell = "min-h-[100dvh] bg-slate-950 text-slate-100";
  const wrap = "max-w-7xl mx-auto px-4 md:px-6 py-6 md:py-8";
  const heading =
    "text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent";
  const panel =
    "bg-slate-900/50 border border-slate-800 rounded-2xl shadow-xl ring-1 ring-white/5 backdrop-blur p-6 md:p-7";
  const label = "block text-xs uppercase tracking-wide text-slate-400 mb-1";
  const input =
    "w-full bg-slate-900/60 border border-slate-700/70 rounded-lg px-3 py-2 text-slate-100 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-emerald-400/60 focus:border-emerald-400/40";
  const btnPrimary =
    "bg-emerald-600 hover:bg-emerald-500 text-white px-4 py-2.5 rounded-lg transition-colors disabled:opacity-60";

  return (
    <div className={shell}>
      <div className={wrap}>
        <h2 className={heading}>Cuenta bancaria</h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4" />

        <div className="max-w-5xl mb-4">
          <ErrorAlert message={error} onClose={() => setError("")} />
          <SuccessAlert message={ok} onClose={() => setOk("")} />
        </div>

        {loading ? (
          <p className="text-slate-300">Cargando...</p>
        ) : yaTieneCuenta ? (
          <RelacionesACHList items={list.slice(0, 1)} />
        ) : (
          <>
            <form onSubmit={onSubmit} className={`${panel} space-y-4`}>
              <div>
                <label className={label}>Titular</label>
                <input
                  className={input}
                  value={titular}
                  onChange={(e) => setTitular(e.target.value)}
                  required
                />
              </div>

              <div className="grid gap-5 md:grid-cols-2">
                <div>
                  <label className={label}>País</label>
                  <select
                    className={input}
                    value={pais}
                    onChange={(e) => setPais(e.target.value)}
                  >
                    <option value="CO">Colombia</option>
                    <option value="EC">Ecuador</option>
                    <option value="VE">Venezuela</option>
                    <option value="US">Estados Unidos</option>
                  </select>
                </div>

                <div>
                  <label className={label}>Banco</label>
                  <select
                    className={input}
                    value={nickname}
                    onChange={(e) => setNickname(e.target.value)}
                    required
                  >
                    <option value="">Selecciona un banco…</option>
                    {bancos.map((b) => (
                      <option key={b.id} value={b.nombre}>
                        {b.nombre}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid gap-5 md:grid-cols-2">
                <div>
                  <label className={label}>Número de cuenta</label>
                  <input
                    className={input}
                    value={numeroCuenta}
                    onChange={onChangeNumero}
                    inputMode="numeric"
                    pattern="[0-9]{8}"
                    maxLength={8}
                    placeholder="8 dígitos"
                    required
                  />
                </div>

                <div>
                  <label className={label}>Tipo de cuenta</label>
                  <select
                    className={input}
                    value={tipoCuenta}
                    onChange={(e) => setTipoCuenta(e.target.value)}
                    required
                  >
                    <option value="CHECKING">Corriente</option>
                    <option value="SAVINGS">Ahorros</option>
                  </select>
                </div>
              </div>

              <button type="submit" className={btnPrimary}>
                Asociar cuenta
              </button>
            </form>

            <div className="mt-6">
              <RelacionesACHList items={list} />
            </div>
          </>
        )}
      </div>
    </div>
  );
}

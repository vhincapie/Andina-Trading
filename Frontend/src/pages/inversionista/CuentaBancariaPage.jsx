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

  const loadList = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await obtenerRelacionesACH();
      setList(Array.isArray(data) ? data : []);
    } catch {
      setError("No se pudieron cargar las cuentas asociadas.");
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
      setError("No se pudo asociar la cuenta bancaria.");
    }
  };

  return (
    <div className="max-w-3xl mx-auto py-6 space-y-6">
      <h2 className="text-2xl font-semibold">Cuenta bancaria</h2>

      <ErrorAlert message={error} onClose={() => setError("")} />
      <SuccessAlert message={ok} onClose={() => setOk("")} />

      {loading ? (
        <p className="text-gray-600">Cargando...</p>
      ) : yaTieneCuenta ? (
        <RelacionesACHList items={list.slice(0, 1)} />
      ) : (
        <>
          <form
            onSubmit={onSubmit}
            className="bg-white border rounded p-4 space-y-3"
          >
            <div>
              <label className="block text-sm mb-1">Titular</label>
              <input
                className="border p-2 rounded w-full"
                value={titular}
                onChange={(e) => setTitular(e.target.value)}
                required
              />
            </div>

            <div>
              <label className="block text-sm mb-1">País</label>
              <select
                className="border p-2 rounded w-full"
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
              <label className="block text-sm mb-1">Banco</label>
              <select
                className="border p-2 rounded w-full"
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

            <div>
              <label className="block text-sm mb-1">Número de cuenta</label>
              <input
                className="border p-2 rounded w-full"
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
              <label className="block text-sm mb-1">Tipo de cuenta</label>
              <select
                className="border p-2 rounded w-full"
                value={tipoCuenta}
                onChange={(e) => setTipoCuenta(e.target.value)}
                required
              >
                <option value="CHECKING">Corriente</option>
                <option value="SAVINGS">Ahorros</option>
              </select>
            </div>

            <button
              type="submit"
              className="bg-blue-600 text-white px-3 py-2 rounded"
            >
              Asociar cuenta
            </button>
          </form>

          <RelacionesACHList items={list} />
        </>
      )}
    </div>
  );
}

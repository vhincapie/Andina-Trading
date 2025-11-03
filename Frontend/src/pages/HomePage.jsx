import { useState } from "react";
import { getMe } from "../api/authService";
import SaldoCard from "../components/inversionista/SaldoCard";
import { useAuth } from "../context/AuthContext";
import MarketChartWidget from "../components/market/MarketChartWidget";

export default function HomePage() {
  const [error, setError] = useState("");
  const { user } = useAuth();

  const role = String(user?.rol || "").toUpperCase();
  const isInvestor = ["INVESTOR", "INVERSIONISTA"].includes(role);
  const isAdmin = ["ADMIN", "ADMINISTRADOR"].includes(role);
  const isComisionista = ["COMISIONISTA"].includes(role);

  const services = [
    {
      key: "servicio-autenticacion",
      name: "Autenticación",
      status: "OK",
      latency: 128,
    },
    {
      key: "servicio-inversionista",
      name: "Inversionistas",
      status: "OK",
      latency: 132,
    },
    { key: "servicio-ordenes", name: "Órdenes", status: "OK", latency: 240 },
    {
      key: "servicio-notificaciones",
      name: "Notificaciones",
      status: "OK",
      latency: 195,
    },
    { key: "servicio-reportes", name: "Reportes", status: "OK", latency: 210 },
    { key: "servicio-respaldo", name: "Respaldo", status: "OK", latency: 170 },
  ];

  const colorByStatus = () => "text-emerald-300 bg-emerald-500/10";
  const dotByStatus = () => "bg-emerald-400";

  return (
    <main className="min-h-[100dvh] bg-slate-950 text-slate-100">
      <div className="max-w-7xl mx-auto px-4 md:px-6 py-6 md:py-8">
        <h2 className="text-2xl font-semibold bg-gradient-to-r from-emerald-400 to-cyan-400 bg-clip-text text-transparent">
          Inicio
        </h2>
        <div className="mt-2 h-0.5 w-20 bg-emerald-400/80 rounded mb-4"></div>

        {error && (
          <div className="mb-4 rounded-lg bg-red-500/10 border border-red-500/30 text-red-200 px-3 py-2 text-sm">
            {error}
          </div>
        )}

        {isAdmin && (
          <div className="space-y-6">
            <section className="rounded-2xl bg-slate-900/50 border border-slate-800 ring-1 ring-white/5 backdrop-blur overflow-hidden">
              <div className="p-4 border-b border-slate-800/70 flex items-center justify-between">
                <div>
                  <h3 className="text-lg font-semibold">
                    Ecosistema de microservicios
                  </h3>
                  <p className="text-xs text-slate-400">Estado y latencia</p>
                </div>
              </div>
              <div className="p-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                {services.map((s) => (
                  <div
                    key={s.key}
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 flex items-center justify-between hover:bg-slate-900/60 transition"
                  >
                    <div className="flex items-center gap-3">
                      <span
                        className={`h-2.5 w-2.5 rounded-full ${dotByStatus(
                          s.status
                        )}`}
                      ></span>
                      <div>
                        <div className="text-sm font-medium text-slate-100">
                          {s.name}
                        </div>
                        <div
                          className={`inline-flex text-[11px] px-2 py-0.5 rounded-full ${colorByStatus(
                            s.status
                          )} mt-1`}
                        >
                          {s.status}
                        </div>
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="text-xs text-slate-400">Latencia</div>
                      <div className="text-sm font-semibold text-slate-100">
                        {s.latency} ms
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </section>

            <section className="grid grid-cols-1 lg:grid-cols-3 gap-4">
              <div className="lg:col-span-2 rounded-2xl bg-slate-900/50 border border-slate-800 ring-1 ring-white/5 backdrop-blur">
                <div className="p-4 border-b border-slate-800/70">
                  <h3 className="text-lg font-semibold">Accesos rápidos</h3>
                  <p className="text-xs text-slate-400">
                    Operaciones frecuentes del administrador
                  </p>
                </div>
                <div className="p-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                  <a
                    href="/admin/comisionistas"
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition text-slate-100"
                  >
                    Comisionistas
                  </a>
                  <a
                    href="/admin/consolidacion"
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition text-slate-100"
                  >
                    Consolidación
                  </a>
                  <a
                    href="/admin/reportes"
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition text-slate-100"
                  >
                    Reportes
                  </a>
                  <a
                    href="/admin/auditoria"
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition text-slate-100"
                  >
                    Auditoría
                  </a>
                  <a
                    href="/admin/respaldo"
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition text-slate-100"
                  >
                    Respaldo
                  </a>
                  <a
                    href="/catalogos/paises"
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition text-slate-100"
                  >
                    Catálogos
                  </a>
                </div>
              </div>

              <div className="rounded-2xl bg-slate-900/50 border border-slate-800 ring-1 ring-white/5 backdrop-blur">
                <div className="p-4 border-b border-slate-800/70">
                  <h3 className="text-lg font-semibold">Estado del sistema</h3>
                  <p className="text-xs text-slate-400">Servicios y tareas</p>
                </div>
                <ul className="p-4 space-y-2 text-sm">
                  <li className="flex items-center justify-between rounded-lg ring-1 ring-slate-800 bg-slate-950/60 px-3 py-2">
                    <span className="text-slate-300">Backups</span>
                    <a
                      href="/admin/respaldo"
                      className="text-cyan-300 text-xs underline underline-offset-4"
                    >
                      Ver
                    </a>
                  </li>
                  <li className="flex items-center justify-between rounded-lg ring-1 ring-slate-800 bg-slate-950/60 px-3 py-2">
                    <span className="text-slate-300">Últimas auditorías</span>
                    <a
                      href="/admin/auditoria"
                      className="text-cyan-300 text-xs underline underline-offset-4"
                    >
                      Abrir
                    </a>
                  </li>
                </ul>
              </div>
            </section>

            <section className="rounded-2xl bg-slate-900/50 border border-slate-800 ring-1 ring-white/5 backdrop-blur overflow-hidden">
              <div className="p-4 border-b border-slate-800/70 flex items-center justify-between">
                <div>
                  <h3 className="text-lg font-semibold">Catálogos</h3>
                  <p className="text-xs text-slate-400">
                    Gestión de países, ciudades y situaciones económicas
                  </p>
                </div>
                <a
                  href="/catalogos/paises"
                  className="text-sm px-3 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 ring-1 ring-slate-700 transition"
                >
                  Abrir
                </a>
              </div>
              <div className="p-4 grid grid-cols-1 sm:grid-cols-3 gap-3">
                <a
                  href="/catalogos/paises"
                  className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition"
                >
                  Países
                </a>
                <a
                  href="/catalogos/ciudades"
                  className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition"
                >
                  Ciudades
                </a>
                <a
                  href="/catalogos/situaciones"
                  className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition"
                >
                  Situaciones
                </a>
              </div>
            </section>
          </div>
        )}

        {isComisionista && (
          <div className="space-y-6">
            <section className="grid grid-cols-1 lg:grid-cols-3 gap-4">
              <div className="lg:col-span-2 rounded-2xl bg-slate-900/50 border border-slate-800 ring-1 ring-white/5 backdrop-blur overflow-hidden">
                <div className="p-4 border-b border-slate-800/70">
                  <h3 className="text-lg font-semibold">Mercado</h3>
                  <p className="text-xs text-slate-400">Vista rápida</p>
                </div>
                <div className="p-4">
                  <div className="rounded-2xl overflow-hidden">
                    <MarketChartWidget
                      symbols="AAPL,MSFT"
                      timeframe="1Day"
                      height={360}
                    />
                  </div>
                </div>
              </div>
              <div className="rounded-2xl bg-slate-900/50 border border-slate-800 ring-1 ring-white/5 backdrop-blur">
                <div className="p-4 border-b border-slate-800/70">
                  <h3 className="text-lg font-semibold">Accesos rápidos</h3>
                  <p className="text-xs text-slate-400">Tareas frecuentes</p>
                </div>
                <div className="p-4 grid grid-cols-1 gap-2">
                  <a
                    href="/comisionista/ordenes"
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition"
                  >
                    Aprobar/Rechazar órdenes
                  </a>
                  <a
                    href="/comisionista/comisiones"
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition"
                  >
                    Resumen de comisiones
                  </a>
                  <a
                    href="/comisionista/perfil"
                    className="rounded-xl ring-1 ring-slate-800 bg-slate-950/60 px-4 py-3 hover:bg-slate-900/60 transition"
                  >
                    Mi perfil
                  </a>
                </div>
              </div>
            </section>
          </div>
        )}

        {isInvestor && (
          <section>
            <div className="flex justify-end mb-6 sticky top-4 z-10">
              <div className="w-full sm:w-auto sm:max-w-sm">
                <SaldoCard onlyBuyingPower />
              </div>
            </div>
            <div className="rounded-2xl overflow-hidden">
              <MarketChartWidget
                symbols="AAPL,MSFT"
                timeframe="1Day"
                height={560}
              />
            </div>
          </section>
        )}
      </div>
    </main>
  );
}

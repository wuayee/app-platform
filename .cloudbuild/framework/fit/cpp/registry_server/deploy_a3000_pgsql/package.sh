#!/bin/bash

set -eux

current_dir=$(dirname $(readlink -f "$0"))

cpp_build_dir=$(readlink -f "$1")
third_party_dir=$(readlink -f "$2")
mkdir -p "$3"
output_dir=$(readlink -f "$3")
package_type=${4:-Debug}

echo "${current_dir}"
echo "${cpp_build_dir}"
echo "${output_dir}"

# 组织运行包
app_dir="${output_dir}"
mkdir -p "${app_dir}"
mkdir -p "${app_dir}/bin"
mkdir -p "${app_dir}/lib"
mkdir -p "${app_dir}/conf"
mkdir -p "${app_dir}/third_party"
mkdir -p "/applog"

# mkdir -p "${app_dir}/sql"
# sql_path=(
#     01_create_fit_registry_database.sql
#     02_create_fit_registry_table.sql
# )
# for sql_file in ${sql_path[@]}
# do
#     cp -av "${cpp_build_dir}/sql/${sql_file}" "${app_dir}/sql"
# done

fit_libs=(
    libFitRuntime.so
    libFitFramework.so
    libFitBrokerClient.so
    libFitBrokerServer.so
    libFitUtil.so
    libsecurec.so

    libFitPlugin_heartbeat_client.so
    libFitPlugin_heartbeat_client.genericables.json
    libFitPlugin_loadbalance.so
    libFitPlugin_loadbalance.genericables.json
    libFitPlugin_registry_address_config.so
    libFitPlugin_registry_address_config.genericables.json
    libFitPlugin_registry_listener.so
    libFitPlugin_registry_listener.json
    libFitPlugin_registry_listener.genericables.json
    libFitPlugin_system_property.so
    libFitPlugin_system_property.genericables.json
    libFitPlugin_broker_http.so
    libFitPlugin_broker_http.genericables.json
    libfit_crypto_scc.so
    libfit_crypto_scc.json

    libFitHeartbeatServerEntity.so
    libregistry_server_repository_pg.so
    libFitPlugin_heartbeat_server.so
    libFitPlugin_heartbeat_server.genericables.json
    libFitPlugin_registry_server.so
    libFitPlugin_registry_server.json
    libFitPlugin_registry_server.genericables.json

    libFitConverterCJson_fit_hakuna_kernel_registry_shared_Address.so
    libFitConverterCJson_fit_hakuna_kernel_registry_shared_Application.so
    libFitConverterCJson_fit_hakuna_kernel_registry_shared_ApplicationInstance.so
    libFitConverterCJson_fit_hakuna_kernel_registry_shared_Endpoint.so
    libFitConverterCJson_fit_hakuna_kernel_registry_shared_FitableInstance.so
    libFitConverterCJson_fit_hakuna_kernel_registry_shared_FitableMeta.so
    libFitConverterCJson_fit_hakuna_kernel_registry_shared_Worker.so
    libFitConverterCJson_fit_hakuna_kernel_shared_Fitable.so
    libFitConverterCJson_fit_heartbeat_heartbeatCommon.so
    libFitConverterCJson_fit_registry_registryCommon.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_notifyFitables.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_queryFitablesAddresses.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_query_running_fitables.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_registerFitables.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_subscribeFitables.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_syncSubscriptionFitService.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_synchronizeFitService.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_unregisterFitables.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_unsubscribeFitables.so
    libFitConverterGJson_fit_heartbeat_heartbeat.so
    libFitConverterGJson_fit_heartbeat_heartbeatAddressChange.so
    libFitConverterGJson_fit_heartbeat_leave.so
    libFitConverterGJson_fit_heartbeat_queryHeartbeatAddressList.so
    libFitConverterGJson_fit_heartbeat_subscribeHeartbeat.so
    libFitConverterGJson_fit_heartbeat_unsubscribeHeartbeat.so
    libFitConverterGJson_fit_hakuna_kernel_registry_shared_query_fitable_metas.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_query_application_instances.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_query_worker_detail.so
    libFitConverterGJson_fit_hakuna_kernel_registry_server_query_all_workers.so
)
for lib in ${fit_libs[@]}
do
    cp -av "${cpp_build_dir}/lib/${lib}" "${app_dir}/lib"
done

third_party_libs=(
    pgsql/install/lib/libpq.so.5
    pgsql/install/lib/libpq.so.5.11
)
for lib in ${third_party_libs[@]}
do
    cp -av "${third_party_dir}/${lib}" "${app_dir}/third_party"
done

cp -av "${cpp_build_dir}/bin/FitWorker" "${app_dir}/bin"
cp -av "${current_dir}/broker_configuration.json" "${app_dir}/conf"
cp -av "${current_dir}/fit_registry_http.json" "${app_dir}/conf"

cp -av "${current_dir}/start.sh" "${app_dir}"
cp -av "${current_dir}/generate_crt.sh" "${app_dir}"

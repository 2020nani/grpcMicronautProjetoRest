package br.com.hernani.recebefuncionarioviagrpc

import br.com.hernani.FuncionarioServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcFuncionarioFactory {

    @Singleton
    fun funcionariostub(@GrpcChannel("funcionario") channel: ManagedChannel): FuncionarioServiceGrpc.FuncionarioServiceBlockingStub? {

        return FuncionarioServiceGrpc.newBlockingStub(channel)

    }
}
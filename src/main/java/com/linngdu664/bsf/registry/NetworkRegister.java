package com.linngdu664.bsf.registry;

import com.linngdu664.bsf.Main;
import com.linngdu664.bsf.network.to_client.*;
import com.linngdu664.bsf.network.to_server.AmmoTypePayload;
import com.linngdu664.bsf.network.to_server.SculkSnowballLauncherSwitchSoundPayload;
import com.linngdu664.bsf.network.to_server.SwitchTweakerStatusModePayload;
import com.linngdu664.bsf.network.to_server.SwitchTweakerTargetModePayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Main.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkRegister {
    @SubscribeEvent
    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ForwardConeParticlesPayload.TYPE, ForwardConeParticlesPayload.STREAM_CODEC, ForwardConeParticlesPayload::handleDataInClient);
        registrar.playToClient(ForwardRaysParticlesPayload.TYPE, ForwardRaysParticlesPayload.STREAM_CODEC, ForwardRaysParticlesPayload::handleDataInClient);
        registrar.playToClient(ImplosionSnowballCannonParticlesPayload.TYPE, ImplosionSnowballCannonParticlesPayload.STREAM_CODEC, ImplosionSnowballCannonParticlesPayload::handleDataInClient);
        registrar.playToClient(ScreenShakePayload.TYPE, ScreenShakePayload.STREAM_CODEC, ScreenShakePayload::handleDataInClient);
        registrar.playToClient(SubspaceSnowballParticlesPayload.TYPE, SubspaceSnowballParticlesPayload.STREAM_CODEC, SubspaceSnowballParticlesPayload::handleDataInClient);
        registrar.playToClient(SubspaceSnowballReleaseTraceParticlesPayload.TYPE, SubspaceSnowballReleaseTraceParticlesPayload.STREAM_CODEC, SubspaceSnowballReleaseTraceParticlesPayload::handleDataInClient);
        registrar.playToClient(TeamMembersPayload.TYPE, TeamMembersPayload.STREAM_CODEC, TeamMembersPayload::handleDataInClient);
        registrar.playToClient(ToggleMovingSoundPayload.TYPE, ToggleMovingSoundPayload.STREAM_CODEC, ToggleMovingSoundPayload::handleDataInClient);
        registrar.playToClient(VectorInversionParticlesPayload.TYPE, VectorInversionParticlesPayload.STREAM_CODEC, VectorInversionParticlesPayload::handleDataInClient);

        registrar.playToServer(AmmoTypePayload.TYPE, AmmoTypePayload.STREAM_CODEC, AmmoTypePayload::handleDataInServer);
        registrar.playToServer(SculkSnowballLauncherSwitchSoundPayload.TYPE, SculkSnowballLauncherSwitchSoundPayload.STREAM_CODEC, SculkSnowballLauncherSwitchSoundPayload::handleDataInServer);
        registrar.playToServer(SwitchTweakerStatusModePayload.TYPE, SwitchTweakerStatusModePayload.STREAM_CODEC, SwitchTweakerStatusModePayload::handleDataInServer);
        registrar.playToServer(SwitchTweakerTargetModePayload.TYPE, SwitchTweakerTargetModePayload.STREAM_CODEC, SwitchTweakerTargetModePayload::handleDataInServer);
    }
}
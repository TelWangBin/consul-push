consul agent -server -ui -bootstrap-expect=1 -data-dir=consul/tmp/ -node=agent-one -advertise=10.10.0.185 -bind=0.0.0.0 -client=0.0.0.0
pause
import bg.sofia.uni.fmi.mjt.order.server.Response;
import bg.sofia.uni.fmi.mjt.order.server.repository.MJTOrderRepository;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Color;
import bg.sofia.uni.fmi.mjt.order.server.tshirt.Size;

public class Main {

    public static void main(String[] args) {
        MJTOrderRepository mjtOrderRepository = new MJTOrderRepository();
        Response resp = mjtOrderRepository.request(Size.S.getName(), Color.RED.getName(), "NARNIA");
        System.out.println(resp.toString());
    }
}

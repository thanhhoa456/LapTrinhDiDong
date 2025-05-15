package com.example.laixea1.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.laixea1.R;
import com.example.laixea1.adapter.TipAdapter;
import com.example.laixea1.entity.Tip;
import java.util.ArrayList;
import java.util.List;

public class TipsActivity extends AppCompatActivity {

    private RecyclerView tipsRecyclerView;
    private TipAdapter tipAdapter;
    private List<Tip> tipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mẹo thi lý thuyết GPLX");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Khởi tạo RecyclerView
        tipsRecyclerView = findViewById(R.id.tipsRecyclerView);
        tipsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo danh sách mẹo
        tipList = new ArrayList<>();
        initializeTips();

        // Thiết lập adapter
        tipAdapter = new TipAdapter(tipList);
        tipsRecyclerView.setAdapter(tipAdapter);
    }

    private void initializeTips() {
        tipList.add(new Tip("Cấp phép", "• Đường cấm dừng, cấm đỗ, cấm đi do UBND cấp tỉnh cấp.\n• Xe quá khổ, quá tải do cơ quan quản lý đường bộ có thẩm quyền cấp phép."));
        tipList.add(new Tip("Nồng độ cồn", "• Người điều khiển xe mô tô, ô tô, máy kéo trên đường mà trong máu hoặc hơi thở có nồng độ cồn: Bị nghiêm cấm."));
        tipList.add(new Tip("Khoảng cách an toàn tối thiểu", "• 35m nếu vận tốc lưu hành (v) = 60 (km/h)\n• 55m nếu 60 < v ≤ 80\n• 70m nếu 80 < v ≤ 100\n• 100m nếu 100 < v ≤ 120\n• Dưới 60km/h: Chủ động và đảm bảo khoảng cách."));
        tipList.add(new Tip("Tuổi lấy bằng lái", "• Tuổi tối đa hạng E: nam 55, nữ 50\n• Tuổi lấy bằng lái xe (cách nhau 3 tuổi):\n  - Gắn máy: 16T (dưới 50cm³)\n  - Mô tô + B1 + B2: 18T\n  - C, FB: 21T\n  - D, FC: 24T\n  - E, FD: 27T"));
        tipList.add(new Tip("Hành vi cấm trên đường cao tốc, hầm, đường vòng, đầu dốc", "• Không được quay đầu xe, không lùi, không vượt.\n• Không được vượt trên cầu hẹp có một làn xe.\n• Không được phép quay đầu xe ở phần đường dành cho người đi bộ qua đường.\n• Cấm lùi xe ở khu vực cấm dừng và nơi đường bộ giao nhau."));
        tipList.add(new Tip("Tại nơi giao nhau không có tín hiệu đèn", "• Có vòng xuyến: Nhường đường bên trái.\n• Không có vòng xuyến: Nhường đường bên phải."));
        tipList.add(new Tip("Niên hạn sử dụng", "• 25 năm: ô tô tải.\n• 20 năm: ô tô chở người trên 9 chỗ."));
        tipList.add(new Tip("Biển báo cấm", "Cấm ô tô (Gồm: mô tô 3 bánh, xe Lam, xe khách) > Cấm xe tải > Cấm Máy kéo > Cấm rơ moóc, sơ mi rơ moóc."));
        tipList.add(new Tip("Thứ tự ưu tiên", "Nhất chớm, nhì ưu, tam đường, tứ hướng:\n1. Nhất chớm: Xe nào chớm tới vạch trước thì được đi trước.\n2. Nhì ưu: Xe ưu tiên được đi trước. Thứ tự: Hỏa - Sự - An - Thương.\n3. Tam đường: Xe ở đường chính, đường ưu tiên.\n4. Tứ hướng: Bên phải trống - Rẽ phải - Đi thẳng - Rẽ trái."));
        tipList.add(new Tip("Các hạng GPLX", "• A1: Mô tô dưới 175 cm³ và xe 3 bánh của người khuyết tật.\n• A2: Mô tô 175 cm³ trở lên.\n• A3: Xe mô tô 3 bánh.\n• B1: Không hành nghề lái xe.\n• B1, B2: Đến 9 chỗ ngồi, xe tải dưới 3.500kg.\n• C: Đến 9 chỗ ngồi, xe trên 3.500kg.\n• D: Chở đến 30 người.\n• E: Chở trên 30 người.\n• FC: C + kéo (ô tô đầu kéo, kéo sơ mi rơ moóc).\n• FE: E + kéo (ô tô chở khách nối toa)."));
        tipList.add(new Tip("Phân nhóm biển báo hiệu", "• Biển nguy hiểm: Hình tam giác vàng.\n• Biển cấm: Vòng tròn đỏ.\n• Biển hiệu lệnh: Vòng tròn xanh.\n• Biển chỉ dẫn: Vuông hoặc chữ nhật xanh.\n• Biển phụ: Vuông, chữ nhật trắng đen, hiệu lực nằm ở biển phụ."));
        tipList.add(new Tip("Tốc độ tối đa trong khu vực đông dân cư", "• 60km/h: Đường đôi hoặc đường 1 chiều có từ 2 làn xe cơ giới trở lên.\n• 50km/h: Đường 2 chiều hoặc đường 1 chiều có 1 làn xe cơ giới."));
        tipList.add(new Tip("Tốc độ tối đa ngoài khu vực đông dân cư", "1. Đường đôi hoặc 1 chiều có ≥2 làn xe cơ giới:\n   - 90km/h: Ô tô con, xe chở người ≤30 chỗ (trừ xe buýt), tải ≤3.5 tấn.\n   - 80km/h: Xe chở người >30 chỗ (trừ xe buýt), tải >3.5 tấn (trừ xitec).\n   - 70km/h: Xe buýt, đầu kéo sơ mi rơ moóc, mô tô, chuyên dùng (trừ trộn vữa, bê tông).\n   - 60km/h: Ô tô kéo rơ moóc, trộn vữa, bê tông, xitec.\n2. Đường 2 chiều hoặc 1 chiều có 1 làn xe cơ giới:\n   - 80km/h: Ô tô con, xe chở người ≤30 chỗ (trừ xe buýt), tải ≤3.5 tấn.\n   - 70km/h: Xe chở người >30 chỗ (trừ xe buýt), tải >3.5 tấn (trừ xitec).\n   - 60km/h: Xe buýt, đầu kéo sơ mi rơ moóc, mô tô, chuyên dùng (trừ trộn vữa, bê tông).\n   - 50km/h: Ô tô kéo rơ moóc, trộn vữa, bê tông, xitec."));
        tipList.add(new Tip("Tốc độ xe máy chuyên dùng", "• Xe máy chuyên dùng, xe gắn máy (kể cả xe máy điện): 40km/h.\n• Trên đường cao tốc: Không vượt quá 120km/h."));
        tipList.add(new Tip("Tăng số, giảm số", "• Tăng 1 giảm 2 (giảm số chọn ý có từ 'về')."));
        tipList.add(new Tip("Phương tiện giao thông", "• Gồm phương tiện giao thông cơ giới và thô sơ đường bộ.\n• Phương tiện tham gia giao thông gồm phương tiện giao thông đường bộ và xe máy chuyên dùng."));
        tipList.add(new Tip("Hiệu lệnh người điều khiển giao thông", "• Giơ tay thẳng đứng: Tất cả dừng, trừ xe trong ngã tư được đi.\n• Giang ngang tay: Trái phải dừng, trước sau đi.\n• Tay phải giơ trước: Sau, phải dừng, trước rẽ phải, trái đi các hướng, người đi bộ qua đường đi sau người điều khiển."));
        tipList.add(new Tip("Khái niệm và quy tắc", "• Các câu có đáp án bị nghiêm cấm, không cho phép hoặc không được phép thì chọn.\n• Tốc độ chậm đi về bên phải.\n• Chỉ dùng còi từ 5h sáng đến 22h tối.\n• Trong đô thị dùng đèn chiếu gần.\n• Không lắp còi đèn không đúng thiết kế.\n• Xe mô tô không kéo xe khác.\n• 5 năm không cấp lại bằng lái khai báo mất.\n• Chuyển làn phải báo trước.\n• Xe thô sơ đi làn bên phải trong cùng.\n• Nhường đường qua đường hẹp, xe lên dốc.\n• Đứng cách ray đường sắt 5m.\n• Vào cao tốc nhường xe đang chạy.\n• Xe dưới 70km/h không vào cao tốc.\n• Trên cao tốc, hầm chỉ dừng/đỗ nơi quy định.\n• Xe quá tải cần giấy phép.\n• Trọng lượng xe kéo rơ moóc lớn hơn rơ moóc.\n• Kéo xe có hãm dùng thanh nối cứng.\n• Xe gắn máy tối đa 40km/h.\n• Đường có giải phân cách là đường đôi.\n• Giảm tốc khi gặp biển nguy hiểm.\n• Giảm tốc, đi sát phải khi xe sau xin vượt.\n• Ưu tiên đường sắt tại giao cắt.\n• Nhường xe ưu tiên có còi, cờ, đèn.\n• Không vượt trên đường vòng, khuất tầm nhìn.\n• Nhường người đi bộ tại vạch kẻ.\n• Dừng/đỗ cách lề đường ≤0.25m.\n• Dừng/đỗ trên đường hẹp cách xe khác 20m.\n• Giảm tốc trên đường ướt, hẹp, đèo dốc.\n• Giảm tốc, từ từ vượt xe buýt đang dừng."));
        tipList.add(new Tip("Nghiệp vụ vận tải", "• Không lái xe liên tục quá 4 giờ.\n• Không làm việc quá 10 giờ/ngày.\n• Không tự ý thay đổi vị trí đón trả khách.\n• Vận chuyển hàng nguy hiểm cần giấy phép."));
        tipList.add(new Tip("Kỹ thuật lái xe", "• Mô tô xuống dốc dùng cả phanh trước và sau.\n• Ô tô số tự động khởi hành đạp phanh hết hành trình.\n• Phanh tay bóp khóa hãm, đẩy cần về phía trước.\n• Ô tô số sàn khởi hành đạp côn hết hành trình.\n• Quay đầu xe với tốc độ thấp.\n• Qua đường sắt không rào chắn: Cách 5m hạ kính, tắt âm thanh, quan sát.\n• Mở cửa xe: Quan sát rồi hé cánh cửa."));
        tipList.add(new Tip("Cấu tạo và sửa chữa", "• Kính chắn gió: Loại kính an toàn.\n• Còi: 90dB đến 115dB.\n• Động cơ diesel không nổ do lẫn tạp chất.\n• Dây đai an toàn hãm khi giật đột ngột.\n• Động cơ 4 kỳ: Pít tông 4 hành trình.\n• Hệ thống bôi trơn giảm ma sát.\n• Niên hạn: Ô tô trên 9 chỗ 20 năm, ô tô tải 25 năm.\n• Động cơ ô tô: Biến nhiệt năng thành cơ năng.\n• Hệ thống truyền lực: Truyền mô men quay.\n• Ly hợp: Truyền/ngắt động cơ đến hộp số.\n• Hộp số: Đảm bảo chuyển động lùi.\n• Hệ thống lái: Thay đổi hướng.\n• Hệ thống phanh: Giảm tốc độ.\n• Ắc quy: Tích trữ điện năng.\n• Khởi động xe tự động: Đạp phanh."));
        tipList.add(new Tip("Quy tắc sa hình", "• Không vòng xuyến: Xe vào trước - Xe ưu tiên - Đường ưu tiên - Đường cùng cấp (bên phải trống - rẽ phải - đi thẳng - rẽ trái).\n• Có vòng xuyến: Chưa vào nhường bên phải, đã vào nhường bên trái.\n• Xe xuống dốc nhường xe lên dốc."));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
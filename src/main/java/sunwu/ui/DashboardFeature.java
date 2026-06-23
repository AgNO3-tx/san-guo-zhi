package sunwu.ui;

/**
 * GUI 中展示的作业功能板块。
 * 这个 record 把 PDF 板块、菜单编号和展示说明放在一起，方便 GUI 和测试共同引用。
 */
public record DashboardFeature(
    String requirementType,
    String pdfSection,
    String menuItems,
    String title,
    String description
) {
    @Override
    public String toString() {
        return title;
    }
}

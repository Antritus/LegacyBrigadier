package bet.astral.flunkie.forge.mixin.client.gui;

import bet.astral.flunkie.forge.client.gui.TextField;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.client.gui.Gui.drawRect;

@Mixin(GuiTextField.class)
public abstract class GuiTextFieldMixin implements TextField {
    @Shadow public abstract boolean getVisible();

    @Shadow public abstract boolean getEnableBackgroundDrawing();

    @Shadow public int xPosition;
    @Shadow public int yPosition;
    @Shadow public int width;
    @Shadow public int height;
    @Shadow private boolean isEnabled;
    @Shadow private int enabledColor;
    @Shadow private int disabledColor;
    @Shadow private int cursorPosition;
    @Shadow private int lineScrollOffset;
    @Shadow private int selectionEnd;
    @Shadow @Final private FontRenderer field_146211_a;
    @Shadow private String text;

    @Shadow public abstract int getWidth();

    @Shadow private boolean isFocused;
    @Shadow private int cursorCounter;
    @Shadow private boolean enableBackgroundDrawing;

    @Shadow public abstract int getMaxStringLength();

    @Shadow protected abstract void drawCursorVertical(int p_146188_1_, int p_146188_2_, int p_146188_3_, int p_146188_4_);

    boolean isLatestRed;

    @Override
    public boolean getRenderLatestRed() {
        return isLatestRed;
    }

    @Override
    public void setRenderLatestRed(boolean render) {
        this.isLatestRed = render;
    }

    @Overwrite
    public void drawTextBox() {
        if (this.getVisible()) {
            if (this.getEnableBackgroundDrawing()) {
                drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, -6250336);
                drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
            }

            int i = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = this.field_146211_a.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
            int l = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
            int i1 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
            int j1 = l;

            if (k > s.length()) {
                k = s.length();
            }

            if (s.length() > 0) {
                String s1 = flag ? s.substring(0, j) : s;
                if (isLatestRed){
                    if (s1.contains(" ")) {
                        String prefix = s1.substring(0, s1.lastIndexOf(" ")) + " ";
                        String red = s1.substring(s1.lastIndexOf(" ") + 1);
                        j1 = this.field_146211_a.drawStringWithShadow(prefix+"§c"+red, l, i1, i);
                    } else {
                        j1 = this.field_146211_a.drawStringWithShadow("§c"+s1, l, i1, i);
                    }
                } else {
                    j1 = this.field_146211_a.drawStringWithShadow(s1, l, i1, i);
                }
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag) {
                k1 = j > 0 ? l + this.width : l;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (s.length() > 0 && flag && j < s.length()) {
                this.field_146211_a.drawStringWithShadow(s.substring(j), j1, i1, i);
            }

            if (flag1) {
                if (flag2) {
                    drawRect(k1, i1 - 1, k1 + 1, i1 + 1 + this.field_146211_a.FONT_HEIGHT, -3092272);
                } else {
                    this.field_146211_a.drawStringWithShadow("_", k1, i1, i);
                }
            }

            if (k != j) {
                int l1 = l + this.field_146211_a.getStringWidth(s.substring(0, k));
                this.drawCursorVertical(k1, i1 - 1, l1 - 1, i1 + 1 + this.field_146211_a.FONT_HEIGHT);
            }
        }
    }
}

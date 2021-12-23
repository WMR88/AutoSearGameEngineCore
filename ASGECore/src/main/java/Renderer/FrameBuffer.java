package Renderer;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
   private int fboID = 0;
   private Texture texture = null;

    public FrameBuffer(int width, int height) {
        // Generate Framebuffer
        fboID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        // create texture to render the data to and attach it to the frame buffer.
        this.texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);

        // create render buffer store the depth info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false : "ERROR: FRAME BUFFER IS NOT COMPLETE";
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getFboID() {
        return fboID;
    }

    public int getTextureId() {
        return texture.getId();
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }
}

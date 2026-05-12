import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getVideo, likeVideo, unlikeVideo, watchVideo } from "../api/videos";

export default function VideoView(){
  const { id } = useParams();
  const [video, setVideo] = useState(null);

  useEffect(() => {
    getVideo(id).then(res => setVideo(res.data)).catch(console.error);
  }, [id]);

  const handleLike = async () => {
    try {
      await likeVideo(id);
      // refresh or update state
    } catch(e){ console.error(e) }
  };

  if(!video) return <div className="p-6">Chargement...</div>;

  return (
    <div className="p-6">
      <h2 className="text-xl font-bold mb-2">{video.title}</h2>
      <video controls src={video.url} className="w-full max-h-[480px] bg-black" />
      <div className="mt-4">
        <button onClick={handleLike} className="px-4 py-2 rounded bg-blue-600 text-white">Like</button>
      </div>
    </div>
  );
}
